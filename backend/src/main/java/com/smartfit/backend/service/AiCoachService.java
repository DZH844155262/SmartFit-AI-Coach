package com.smartfit.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartfit.backend.client.DeepSeekClient;
import com.smartfit.backend.dto.ai.AiCoachRequest;
import com.smartfit.backend.dto.ai.AiCoachResponse;
import com.smartfit.backend.entity.AiTrainingAnalysis;
import com.smartfit.backend.entity.FitnessProfile;
import com.smartfit.backend.exception.BusinessException;
import com.smartfit.backend.mapper.AiTrainingAnalysisMapper;
import com.smartfit.backend.mapper.FitnessProfileMapper;
import com.smartfit.backend.vo.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.smartfit.backend.entity.TrainingSession;
import com.smartfit.backend.mapper.TrainingPlanExerciseMapper;
import com.smartfit.backend.mapper.TrainingSessionMapper;
import com.smartfit.backend.vo.TrainingPlanExerciseVO;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class AiCoachService {

    private final TrainingService trainingService;
    private final FitnessProfileMapper fitnessProfileMapper;
    private final DeepSeekClient deepSeekClient;
    private final ObjectMapper objectMapper;
    private final AiTrainingAnalysisMapper aiTrainingAnalysisMapper;

    private final String model;

    // 以后Prompt发生明显修改，就改成v2
    private static final String PROMPT_VERSION = "v2";

    private final TrainingSessionMapper trainingSessionMapper;

    private final TrainingPlanExerciseMapper trainingPlanExerciseMapper;

    public AiCoachService(
            TrainingService trainingService,
            FitnessProfileMapper fitnessProfileMapper,
            DeepSeekClient deepSeekClient,
            ObjectMapper objectMapper,
            AiTrainingAnalysisMapper aiTrainingAnalysisMapper,
            TrainingSessionMapper trainingSessionMapper,
            TrainingPlanExerciseMapper trainingPlanExerciseMapper,
            @Value("${deepseek.model}") String model
    ) {
        this.trainingService = trainingService;
        this.fitnessProfileMapper = fitnessProfileMapper;
        this.deepSeekClient = deepSeekClient;
        this.objectMapper = objectMapper;
        this.aiTrainingAnalysisMapper = aiTrainingAnalysisMapper;
        this.trainingSessionMapper = trainingSessionMapper;
        this.trainingPlanExerciseMapper = trainingPlanExerciseMapper;
        this.model = model;
    }


    /*
     * =========================================================
     * 1. 收集准备发送给AI的真实训练数据
     * =========================================================
     */
    public AiCoachRequest buildCoachRequest(Long sessionId) {

        // 查询本次训练详情
        TrainingSessionDetailVO session =
                trainingService.getSessionDetail(sessionId);


        // 查询用户健身档案
        FitnessProfile profile =
                fitnessProfileMapper.findByUserId(
                        session.getUserId()
                );

        if (profile == null) {
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "用户尚未完成健身建档"
            );
        }


        // 本次训练统计
        TrainingSessionSummaryVO sessionSummary =
                trainingService.getSessionSummary(
                        sessionId
                );


        // 计划 vs 实际完成情况
        TrainingPlanComparisonVO planComparison =
                trainingService.getPlanComparison(
                        sessionId
                );


        // 查询本次计划中所有动作的历史趋势
        List<ExerciseTrendVO> exerciseTrends =
                new ArrayList<>();

        for (PlanExerciseComparisonVO exercise
                : planComparison.getExercises()) {

            ExerciseTrendVO trend =
                    trainingService.getExerciseTrend(
                            session.getUserId(),
                            exercise.getExerciseId(),
                            20
                    );

            exerciseTrends.add(trend);
        }


        // 组装发送给AI的数据
        AiCoachRequest request =
                new AiCoachRequest();

        request.setUserId(
                session.getUserId()
        );

        request.setSessionId(
                sessionId
        );

        request.setGoal(
                profile.getGoal()
        );

        request.setExperienceLevel(
                profile.getExperienceLevel()
        );

        request.setSessionSummary(
                sessionSummary
        );

        request.setPlanComparison(
                planComparison
        );

        request.setExerciseTrends(
                exerciseTrends
        );


        return request;
    }


    /*
     * =========================================================
     * 2. 普通AI分析
     *
     * 先查数据库。
     * 有历史结果就直接返回，不调用DeepSeek。
     * =========================================================
     */
    public AiCoachResponse analyzeSession(Long sessionId) {

        AiTrainingAnalysis cached =
                aiTrainingAnalysisMapper.findLatest(
                        sessionId,
                        model,
                        PROMPT_VERSION
                );


        // 数据库已经有分析结果
        if (cached != null) {

            return convertToResponse(
                    cached
            );
        }


        // 数据库没有，才真正调用AI
        return generateAndSaveAnalysis(
                sessionId
        );
    }


    /*
     * =========================================================
     * 3. 强制重新生成
     *
     * 不读取旧分析，直接重新调用DeepSeek。
     * =========================================================
     */
    public AiCoachResponse regenerateSession(Long sessionId) {

        return generateAndSaveAnalysis(
                sessionId
        );
    }


    /*
     * =========================================================
     * 4. 真正调用DeepSeek + 保存结果
     *
     * analyzeSession 和 regenerateSession
     * 都可以复用这个方法。
     * =========================================================
     */
    private AiCoachResponse generateAndSaveAnalysis(
            Long sessionId
    ) {

        // 先准备AI上下文
        AiCoachRequest context =
                buildCoachRequest(
                        sessionId
                );


        try {

            // Java对象 → JSON
            String contextJson =
                    objectMapper.writeValueAsString(
                            context
                    );


            String systemPrompt = """
                    你是 SmartFit AI Coach，一个训练数据分析助手。

                    请根据系统提供的真实训练数据，
                    对本次训练进行简洁、谨慎、可执行的评价。

                    必须遵守：

                    1. 只能使用输入JSON已有的数据。
                    2. 不要自行重新计算完成率、训练容量和趋势百分比。
                    3. 不要编造用户没有提供的信息。
                    4. 不进行疾病、损伤或医疗诊断。
                    5. 数据不足时必须明确说明。
                    6. 下一次训练建议必须具体可执行。
                    7. positiveSignals、riskSignals、nextSessionAdvice每项最多3条。
                    8. score必须是0到100之间的整数。
                    9. 必须只返回合法JSON，不要返回Markdown代码块。

                    返回格式必须是：
                    
                     {
                       "score": 82,
                       "summary": "本次训练总体评价",
                       "positiveSignals": [
                         "积极信号1"
                       ],
                       "riskSignals": [
                         "需要注意的信号1"
                       ],
                       "nextSessionAdvice": [
                         "下一次训练建议1"
                       ],
                       "planAdjustments": [
                         {
                           "exerciseId": 1,
                           "exerciseName": "杠铃卧推",
                           "action": "KEEP",
                           "currentWeight": 60,
                           "recommendedWeight": 60,
                           "targetSets": 3,
                           "targetRepsMin": 8,
                           "targetRepsMax": 10,
                           "targetRpe": 8,
                           "reason": "根据本次实际表现，下一次先保持当前重量并完成目标次数区间。"
                         }
                       ]
                     }
                    
                     planAdjustments必须遵守：
                    
                     1. 本次计划中每个动作最多返回一条调整建议。
                     2. exerciseId必须使用输入JSON中的真实exerciseId。
                     3. exerciseName必须使用输入JSON中的真实动作名称。
                     4. action只能是：
                        KEEP
                        INCREASE
                        DECREASE
                        ADJUST
                     5. 不允许编造新的动作。
                     6. recommendedWeight必须是明确数值；如果建议保持，则与currentWeight相同。
                     7. 不确定时优先KEEP，不要激进增加重量。
                     8. reason必须结合本次训练数据和历史趋势解释。
                     9. 这只是计划调整建议，不代表用户已经确认修改训练计划。
                    """;


            String userPrompt =
                    """
                    请根据下面的 SmartFit 训练上下文进行分析。

                    请严格返回JSON。

                    训练上下文：
                    """
                            + contextJson;


            // 真正调用DeepSeek
            String aiJson =
                    deepSeekClient.generateJson(
                            systemPrompt,
                            userPrompt
                    );


            // DeepSeek JSON → Java对象
            AiCoachResponse response =
                    objectMapper.readValue(
                            aiJson,
                            AiCoachResponse.class
                    );


            // 校验AI输出
            validateAiResponse(
                    response
            );


            // 保存AI分析结果
            saveAnalysis(
                    context,
                    response,
                    aiJson
            );


            return response;


        } catch (JsonProcessingException e) {

            throw new BusinessException(
                    HttpStatus.BAD_GATEWAY,
                    "AI返回格式解析失败"
            );
        }
    }


    /*
     * =========================================================
     * 5. 把AI分析保存到MySQL
     * =========================================================
     */
    private void saveAnalysis(
            AiCoachRequest context,
            AiCoachResponse response,
            String rawResponse
    ) throws JsonProcessingException {

        AiTrainingAnalysis analysis =
                new AiTrainingAnalysis();


        analysis.setUserId(
                context.getUserId()
        );

        analysis.setSessionId(
                context.getSessionId()
        );

        analysis.setModel(
                model
        );

        analysis.setScore(
                response.getScore()
        );

        analysis.setSummary(
                response.getSummary()
        );


        // List<String> → JSON字符串
        analysis.setPositiveSignals(
                objectMapper.writeValueAsString(
                        response.getPositiveSignals() == null
                                ? Collections.emptyList()
                                : response.getPositiveSignals()
                )
        );


        analysis.setRiskSignals(
                objectMapper.writeValueAsString(
                        response.getRiskSignals() == null
                                ? Collections.emptyList()
                                : response.getRiskSignals()
                )
        );


        analysis.setNextSessionAdvice(
                objectMapper.writeValueAsString(
                        response.getNextSessionAdvice() == null
                                ? Collections.emptyList()
                                : response.getNextSessionAdvice()
                )
        );

        analysis.setPlanAdjustments(
                objectMapper.writeValueAsString(
                        response.getPlanAdjustments() == null
                                ? Collections.emptyList()
                                : response.getPlanAdjustments()
                )
        );


        analysis.setPromptVersion(
                PROMPT_VERSION
        );


        // 保存DeepSeek当时返回的原始JSON
        analysis.setRawResponse(
                rawResponse
        );


        aiTrainingAnalysisMapper.insert(
                analysis
        );
    }


    /*
     * =========================================================
     * 6. 把数据库记录恢复成前端需要的AiCoachResponse
     * =========================================================
     */
    private AiCoachResponse convertToResponse(
            AiTrainingAnalysis analysis
    ) {

        try {

            AiCoachResponse response =
                    new AiCoachResponse();


            response.setScore(
                    analysis.getScore()
            );

            response.setSummary(
                    analysis.getSummary()
            );


            response.setPositiveSignals(
                    objectMapper.readValue(
                            analysis.getPositiveSignals(),
                            new TypeReference<List<String>>() {
                            }
                    )
            );


            response.setRiskSignals(
                    objectMapper.readValue(
                            analysis.getRiskSignals(),
                            new TypeReference<List<String>>() {
                            }
                    )
            );


            response.setNextSessionAdvice(
                    objectMapper.readValue(
                            analysis.getNextSessionAdvice(),
                            new TypeReference<List<String>>() {
                            }
                    )
            );

            if (analysis.getPlanAdjustments() != null
                    && !analysis.getPlanAdjustments().isBlank()) {

                response.setPlanAdjustments(
                        objectMapper.readValue(
                                analysis.getPlanAdjustments(),
                                new TypeReference<List<PlanAdjustmentProposalVO>>() {
                                }
                        )
                );

            } else {

                response.setPlanAdjustments(
                        Collections.emptyList()
                );
            }

            return response;


        } catch (JsonProcessingException e) {

            throw new BusinessException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "读取AI历史分析失败"
            );
        }
    }


    /*
     * =========================================================
     * 7. 检查大模型输出是否合法
     * =========================================================
     */
    private void validateAiResponse(
            AiCoachResponse response
    ) {

        if (response == null) {
            throw new BusinessException(
                    HttpStatus.BAD_GATEWAY,
                    "AI返回结果为空"
            );
        }


        if (response.getScore() == null
                || response.getScore() < 0
                || response.getScore() > 100) {

            throw new BusinessException(
                    HttpStatus.BAD_GATEWAY,
                    "AI返回评分格式异常"
            );
        }


        if (response.getSummary() == null
                || response.getSummary().isBlank()) {

            throw new BusinessException(
                    HttpStatus.BAD_GATEWAY,
                    "AI返回总结为空"
            );
        }

        if (response.getPlanAdjustments() == null) {
            throw new BusinessException(
                    HttpStatus.BAD_GATEWAY,
                    "AI未返回训练计划调整建议"
            );
        }
    }
    @Transactional
    public void applyPlanAdjustments(Long analysisId) {

        /*
         * 1. 找到这次AI分析
         */
        AiTrainingAnalysis analysis =
                aiTrainingAnalysisMapper.findById(
                        analysisId
                );

        if (analysis == null) {
            throw new BusinessException(
                    HttpStatus.NOT_FOUND,
                    "AI训练分析不存在"
            );
        }


        /*
         * 2. 防止同一个Proposal重复应用
         */
        if (Boolean.TRUE.equals(
                analysis.getApplied()
        )) {
            throw new BusinessException(
                    HttpStatus.CONFLICT,
                    "该训练调整建议已经应用"
            );
        }


        /*
         * 3. 找到这次训练
         */
        TrainingSession session =
                trainingSessionMapper.findById(
                        analysis.getSessionId()
                );

        if (session == null) {
            throw new BusinessException(
                    HttpStatus.NOT_FOUND,
                    "训练记录不存在"
            );
        }


        /*
         * 自由训练没有planDayId，
         * 就不存在可以修改的计划模板
         */
        if (session.getPlanDayId() == null) {
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "该训练不是计划训练，无法应用计划调整"
            );
        }


        try {

            /*
             * 4. 数据库JSON → Java Proposal
             */
            List<PlanAdjustmentProposalVO> proposals =
                    objectMapper.readValue(
                            analysis.getPlanAdjustments(),
                            new TypeReference<
                                    List<PlanAdjustmentProposalVO>
                                    >() {
                            }
                    );


            if (proposals == null
                    || proposals.isEmpty()) {

                throw new BusinessException(
                        HttpStatus.BAD_REQUEST,
                        "该AI分析没有可应用的训练调整"
                );
            }


            /*
             * 5. 查询现在数据库中真实的训练计划
             */
            List<TrainingPlanExerciseVO> currentExercises =
                    trainingPlanExerciseMapper.findByPlanDayId(
                            session.getPlanDayId()
                    );


            /*
             * 6. 一条一条校验 + 应用
             */
            for (PlanAdjustmentProposalVO proposal
                    : proposals) {

                TrainingPlanExerciseVO current =
                        currentExercises
                                .stream()
                                .filter(
                                        exercise ->
                                                exercise.getExerciseId()
                                                        .equals(
                                                                proposal.getExerciseId()
                                                        )
                                )
                                .findFirst()
                                .orElseThrow(
                                        () -> new BusinessException(
                                                HttpStatus.BAD_REQUEST,
                                                "AI建议包含当前计划中不存在的动作"
                                        )
                                );


                /*
                 * 7. 防止应用一个已经过期的Proposal
                 *
                 * 例如：
                 * AI生成建议时计划重量是60kg，
                 * 但用户后来手动改成了62.5kg。
                 *
                 * 这时不能再偷偷套用旧AI建议。
                 */
                if (!sameWeight(
                        current.getTargetWeightKg(),
                        proposal.getCurrentWeight()
                )) {

                    throw new BusinessException(
                            HttpStatus.CONFLICT,
                            "训练计划已经发生变化，请重新生成AI分析"
                    );
                }


                /*
                 * 8. 基础安全校验
                 */
                validatePlanAdjustment(
                        proposal
                );


                /*
                 * 9. 真正更新计划
                 */
                int updated =
                        trainingPlanExerciseMapper.updateTargets(
                                session.getPlanDayId(),
                                proposal.getExerciseId(),
                                proposal.getRecommendedWeight(),
                                proposal.getTargetSets(),
                                proposal.getTargetRepsMin(),
                                proposal.getTargetRepsMax(),
                                proposal.getTargetRpe()
                        );


                if (updated != 1) {
                    throw new BusinessException(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            "更新训练计划失败"
                    );
                }
            }


            /*
             * 10. 整批修改成功后，
             * 才把AI分析标记为已应用
             */
            int marked =
                    aiTrainingAnalysisMapper.markAsApplied(
                            analysisId
                    );


            if (marked != 1) {
                throw new BusinessException(
                        HttpStatus.CONFLICT,
                        "训练调整建议状态更新失败"
                );
            }


        } catch (JsonProcessingException e) {

            throw new BusinessException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "读取AI训练调整建议失败"
            );
        }
    }
    private boolean sameWeight(
            BigDecimal first,
            BigDecimal second
    ) {

        if (first == null && second == null) {
            return true;
        }

        if (first == null || second == null) {
            return false;
        }

        return first.compareTo(second) == 0;
    }
    private void validatePlanAdjustment(
            PlanAdjustmentProposalVO proposal
    ) {

        List<String> allowedActions =
                List.of(
                        "KEEP",
                        "INCREASE",
                        "DECREASE",
                        "ADJUST"
                );


        if (proposal.getAction() == null
                || !allowedActions.contains(
                proposal.getAction()
        )) {

            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "AI返回了非法训练调整类型"
            );
        }


        if (proposal.getTargetSets() == null
                || proposal.getTargetSets() <= 0) {

            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "AI返回了非法目标组数"
            );
        }


        if (proposal.getTargetRepsMin() == null
                || proposal.getTargetRepsMax() == null
                || proposal.getTargetRepsMin() <= 0
                || proposal.getTargetRepsMin()
                > proposal.getTargetRepsMax()) {

            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "AI返回了非法目标次数范围"
            );
        }


        if (proposal.getRecommendedWeight() != null
                && proposal.getRecommendedWeight()
                .compareTo(BigDecimal.ZERO) < 0) {

            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "AI返回了非法训练重量"
            );
        }
    }
}