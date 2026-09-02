package com.smartfit.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartfit.backend.client.DeepSeekClient;
import com.smartfit.backend.dto.ai.AiCoachRequest;
import com.smartfit.backend.dto.ai.AiCoachResponse;
import com.smartfit.backend.entity.AiTrainingAnalysis;
import com.smartfit.backend.entity.FitnessProfile;
import com.smartfit.backend.entity.TrainingSession;
import com.smartfit.backend.exception.BusinessException;
import com.smartfit.backend.mapper.AiTrainingAnalysisMapper;
import com.smartfit.backend.mapper.FitnessProfileMapper;
import com.smartfit.backend.mapper.TrainingPlanExerciseMapper;
import com.smartfit.backend.mapper.TrainingSessionMapper;
import com.smartfit.backend.vo.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.time.LocalDate;

@Slf4j
@Service
public class AiCoachService {

    private final TrainingService trainingService;
    private final FitnessProfileMapper fitnessProfileMapper;
    private final DeepSeekClient deepSeekClient;
    private final ObjectMapper objectMapper;
    private final AiTrainingAnalysisMapper aiTrainingAnalysisMapper;
    private final TrainingSessionMapper trainingSessionMapper;
    private final TrainingPlanExerciseMapper trainingPlanExerciseMapper;
    private final EvidenceAgentService evidenceAgentService;

    private final NutritionService nutritionService;

    private final String model;


    /*
     * v1：AI训练点评
     * v2：增加结构化 planAdjustments
     * v3：增加 PubMed Evidence Agent
     */
    private static final String PROMPT_VERSION = "v3";


    public AiCoachService(
            TrainingService trainingService,
            NutritionService nutritionService,
            FitnessProfileMapper fitnessProfileMapper,
            DeepSeekClient deepSeekClient,
            ObjectMapper objectMapper,
            AiTrainingAnalysisMapper aiTrainingAnalysisMapper,
            TrainingSessionMapper trainingSessionMapper,
            TrainingPlanExerciseMapper trainingPlanExerciseMapper,
            EvidenceAgentService evidenceAgentService,
            @Value("${deepseek.model}") String model
    ) {

        this.trainingService =
                trainingService;

        this.nutritionService =
                nutritionService;

        this.fitnessProfileMapper =
                fitnessProfileMapper;

        this.deepSeekClient =
                deepSeekClient;

        this.objectMapper =
                objectMapper;

        this.aiTrainingAnalysisMapper =
                aiTrainingAnalysisMapper;

        this.trainingSessionMapper =
                trainingSessionMapper;

        this.trainingPlanExerciseMapper =
                trainingPlanExerciseMapper;

        this.evidenceAgentService =
                evidenceAgentService;

        this.model =
                model;
    }


    /*
     * =========================================================
     * 1. 收集准备发送给AI的真实训练数据
     * =========================================================
     */
    public AiCoachRequest buildCoachRequest(
            Long sessionId
    ) {

        /*
         * 查询本次训练详情
         */
        TrainingSessionDetailVO session =
                trainingService.getSessionDetail(
                        sessionId
                );


        /*
         * 获取用户健身档案
         */
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


        /*
         * 本次训练统计
         */
        TrainingSessionSummaryVO sessionSummary =
                trainingService.getSessionSummary(
                        sessionId
                );


        /*
         * 计划 vs 实际
         */
        TrainingPlanComparisonVO planComparison =
                trainingService.getPlanComparison(
                        sessionId
                );


        /*
         * 查询本次计划动作的历史趋势
         */
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


            exerciseTrends.add(
                    trend
            );
        }

        /*
         * =========================================================
         * Nutrition Context
         * =========================================================
         */
        LocalDate sessionDate =
                session.getSessionDate();


        List<DailyNutritionSummaryVO> dailyNutrition =
                nutritionService.getNutritionSummary(
                        session.getUserId(),
                        sessionDate,
                        sessionDate
                );


        DailyNutritionSummaryVO nutritionOnSessionDate =
                dailyNutrition.isEmpty()
                        ? null
                        : dailyNutrition.get(0);



        NutritionPeriodSummaryVO recentNutrition7Days =
                nutritionService.getNutritionPeriodSummary(
                        session.getUserId(),
                        sessionDate.minusDays(6),
                        sessionDate
                );

        /*
         * 组装AI Context
         */
        AiCoachRequest request =
                new AiCoachRequest();

        request.setSessionDate(
                sessionDate
        );

        request.setNutritionOnSessionDate(
                nutritionOnSessionDate
        );

        request.setRecentNutrition7Days(
                recentNutrition7Days
        );

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
     *
     * 有：
     * 直接返回历史结果。
     *
     * 没有：
     * Evidence Agent + DeepSeek重新生成。
     * =========================================================
     */
    public AiCoachResponse analyzeSession(
            Long sessionId
    ) {


        AiTrainingAnalysis cached =
                aiTrainingAnalysisMapper.findLatest(
                        sessionId,
                        model,
                        PROMPT_VERSION
                );


        if (cached != null) {

            log.info(
                    "AI analysis cache hit, sessionId={}, analysisId={}",
                    sessionId,
                    cached.getId()
            );


            return convertToResponse(
                    cached
            );
        }

        log.info(
                "AI analysis cache miss, generating new analysis, sessionId={}",
                sessionId
        );


        return generateAndSaveAnalysis(
                sessionId
        );
    }


    /*
     * =========================================================
     * 3. 强制重新分析
     *
     * 不读取缓存。
     * =========================================================
     */
    public AiCoachResponse regenerateSession(
            Long sessionId
    ) {

        return generateAndSaveAnalysis(
                sessionId
        );
    }


    /*
     * =========================================================
     * 4. 真正执行：
     *
     * 用户训练数据
     * ↓
     * Evidence Agent
     * ↓
     * PubMed
     * ↓
     * 科学Evidence
     * ↓
     * 最终AI Coach
     * ↓
     * 保存结果
     * =========================================================
     */
    private AiCoachResponse generateAndSaveAnalysis(
            Long sessionId
    ) {

        log.info(
                "Start AI coach analysis, sessionId={}",
                sessionId
        );


        AiCoachRequest context =
                buildCoachRequest(
                        sessionId
                );


        String aiJson = null;


        try {


            /*
             * Context → JSON
             */
            String contextJson =
                    objectMapper.writeValueAsString(
                            context
                    );


            /*
             * 2. 根据用户数据构造Evidence问题
             */
            String evidenceQuestion =
                    buildEvidenceQuestion(
                            contextJson
                    );


            /*
             * 3. Evidence Agent
             *
             * 内部流程：
             *
             * DeepSeek
             * ↓
             * search_papers
             * ↓
             * PubMed
             * ↓
             * Evidence
             */
            EvidenceAgentResponseVO evidence =
                    evidenceAgentService
                            .answerWithEvidence(
                                    evidenceQuestion
                            );


            /*
             * Evidence → JSON
             */
            String evidenceJson =
                    objectMapper.writeValueAsString(
                            evidence
                    );


            /*
             * =================================================
             * 最终AI Coach Prompt
             * =================================================
             */
            String systemPrompt = """
                    你是 SmartFit AI Coach。

                    你的任务是根据：
                    
                    1. 用户真实训练数据；
                    2. 已计算好的训练统计；
                    3. 计划与实际完成情况；
                    4. 历史动作趋势；
                    5. 用户饮食记录及营养统计；
                    6. PubMed Evidence Agent提供的科学证据；

                    对本次训练进行分析，
                    并生成下一次训练计划的结构化调整建议。


                    必须遵守以下规则：

                    1. 用户训练数据是事实来源，
                       不允许编造用户没有提供的数据。

                    2. completionRate、volume、RPE、
                       trend percentage等确定性指标，
                       使用Java已经计算好的结果，
                       不要自行重新计算。

                    3. 科学训练结论优先依据
                       Evidence Agent提供的PubMed证据。

                    4. 不允许编造论文、
                       PMID或研究结论。

                    5. 如果Evidence中的limitations说明
                       当前证据不足或属于间接证据，
                       最终建议必须更加谨慎。

                    6. 不进行疾病、损伤或医疗诊断。

                    7. 数据不足时必须明确说明，
                       不允许为了给建议而强行下结论。

                    8. 不确定是否应该增加训练负荷时，
                       优先给出保守的KEEP建议，
                       而不是激进增加重量。

                    9. planAdjustments只是建议，
                       不能假设数据库里的训练计划已经修改。
                    
                    10. 关于营养分析：
                    
                        a.
                        nutritionOnSessionDate代表训练当天饮食记录。
                    
                        b.
                        recentNutrition7Days代表最近7天饮食记录情况。
                    
                        c.
                        recordedDays和recordCoveragePercent代表数据完整程度。
                    
                        d.
                        如果饮食记录天数不足，
                           不允许推断用户长期饮食习惯。
                    
                        e.
                        如果记录不足，
                           必须明确说明：
                           "当前饮食数据不足，无法进行长期判断。"
                    
                        f.
                        营养建议只能作为辅助建议，
                           不进行医学或疾病相关判断。
                    11. positiveSignals、
                        riskSignals、
                        nextSessionAdvice
                        每项最多3条。

                    12. score必须是0到100的整数。

                    13. 必须只返回合法JSON，
                        不要返回Markdown代码块。


                    返回JSON格式必须为：

                    {
                      "score":82,
                    
                      "summary":
                        "本次训练总体评价",
                    
                    
                      "nutritionAnalysis":
                      {
                        "status":
                          "INSUFFICIENT_DATA",
                    
                        "summary":
                          "营养情况分析",
                    
                        "suggestions":
                        [
                          "营养建议1"
                        ]
                      },
                    
                    
                      "positiveSignals":
                      [
                        "积极信号1"
                      ],
                    
                    
                      "riskSignals":
                      [
                        "风险信号1"
                      ],
                    
                    
                      "nextSessionAdvice":
                      [
                        "下一次建议1"
                      ],
                    
                    
                      "planAdjustments":
                      [
                        {
                          "exerciseId":1,
                          "exerciseName":"杠铃卧推",
                          "action":"KEEP",
                          "currentWeight":60,
                          "recommendedWeight":60,
                          "targetSets":3,
                          "targetRepsMin":8,
                          "targetRepsMax":10,
                          "targetRpe":8,
                          "reason":"调整理由"
                        }
                      ]
                    }


                    planAdjustments必须遵守：

                    1. 每个计划动作最多一条调整建议。

                    2. exerciseId必须来自训练上下文。

                    3. exerciseName必须来自训练上下文。

                    4. action只能是：

                       KEEP
                       INCREASE
                       DECREASE
                       ADJUST

                    5. 不允许新增训练上下文中不存在的动作。

                    6. recommendedWeight必须是明确数值。

                       KEEP时：
                       recommendedWeight必须与currentWeight一致。

                    7. targetSets、
                       targetRepsMin、
                       targetRepsMax、
                       targetRpe
                       必须是真实可执行的下一次训练目标。

                    8. reason必须同时说明：

                       用户数据为什么支持这个建议；

                       Evidence如何影响这个判断。

                    9. Evidence不足时，
                       不要假装存在确定科学结论。
                    
                    10. status只能为：
                       SUFFICIENT_DATA
                       INSUFFICIENT_DATA
                    
                    11. 必须根据nutritionOnSessionDate和recentNutrition7Days判断。
                    
                    12. 如果recentNutrition7Days.recordedDays较少，
                       或recordCoveragePercent较低：
                    
                       必须说明饮食数据不足。
                    
                    13. 不允许根据单次饮食记录推断长期饮食习惯。
                    
                    14. suggestions最多3条。
                    """;


            /*
             * =================================================
             * 用户Prompt
             * =================================================
             */
            String userPrompt =
                    """
                    请根据下面两部分信息，
                    对本次训练进行分析。


                    ===== PART 1: 用户真实训练上下文 =====

                    """
                            + contextJson
                            +
                            """
        
        
                            ===== PART 2: PubMed科学证据 =====
        
                            """
                            + evidenceJson
                            +
                            """
        
        
                            请结合用户真实训练表现和科学证据，
                            生成最终JSON结果。
        
                            不允许返回JSON以外的内容。
                            """;


            /*
             * 4. 最终调用DeepSeek
             */
            aiJson =
                    deepSeekClient.generateJson(
                            systemPrompt,
                            userPrompt
                    );


            /*
             * 5. JSON → Java
             */
            AiCoachResponse response =
                    objectMapper.readValue(
                            aiJson,
                            AiCoachResponse.class
                    );


            /*
             * 6. Java验证AI输出
             */
            validateAiResponse(
                    response
            );


            /*
             * 7. 保存数据库
             */
            saveAnalysis(
                    context,
                    response,
                    aiJson
            );
            log.info(
                    "AI coach analysis completed, sessionId={}",
                    sessionId
            );


            return response;


        } catch (JsonProcessingException e) {


            log.error(
                    "AI response JSON parse failed, rawResponse={}",
                    aiJson,
                    e
            );


            throw new BusinessException(
                    HttpStatus.BAD_GATEWAY,
                    "AI返回格式解析失败"
            );
        }
    }


    /*
     * =========================================================
     * 5. 根据用户训练数据构造 Evidence Question
     * =========================================================
     */
    private String buildEvidenceQuestion(
            String contextJson
    ) {


        return """
                You are researching scientific evidence
                for a personalized resistance-training decision.

                The following JSON contains a real user's:

                - training goal;
                - experience level;
                - latest session summary;
                - planned versus actual performance;
                - exercise history and trends.

                USER TRAINING CONTEXT:

                """
                + contextJson
                +
                """

                Based on this training context,
                identify the most relevant exercise-science question
                needed to decide the user's next training progression.

                Search scientific evidence about topics such as:

                - progressive overload;
                - resistance-training progression;
                - hypertrophy;
                - strength development;
                - repetition ranges;
                - RPE / RIR;
                - proximity to failure;
                - load progression;
                - training volume.

                Focus only on evidence that can materially affect
                the next-session training decision.

                The goal is NOT to provide generic fitness advice.

                The goal is to determine what current scientific
                evidence suggests for a user with THIS training context.
                """;
    }


    /*
     * =========================================================
     * 6. 保存AI分析到数据库
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


        /*
         * List<String>
         * ↓
         * JSON String
         */
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


        /*
         * PlanAdjustmentProposalVO List
         * ↓
         * JSON
         */
        analysis.setPlanAdjustments(
                objectMapper.writeValueAsString(

                        response.getPlanAdjustments() == null

                                ? Collections.emptyList()

                                : response.getPlanAdjustments()
                )
        );
        analysis.setNutritionAnalysis(
                objectMapper.writeValueAsString(
                        response.getNutritionAnalysis()
                )
        );


        analysis.setPromptVersion(
                PROMPT_VERSION
        );


        /*
         * 保存最终DeepSeek原始JSON
         */
        analysis.setRawResponse(
                rawResponse
        );


        aiTrainingAnalysisMapper.insert(
                analysis
        );
        analysis.setNutritionAnalysis(
                objectMapper.writeValueAsString(
                        response.getNutritionAnalysis()
                )
        );
    }


    /*
     * =========================================================
     * 7. 数据库对象 → API返回对象
     *
     * AiTrainingAnalysis
     * ↓
     * AiCoachResponse
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
                    readStringList(
                            analysis.getPositiveSignals()
                    )
            );


            response.setRiskSignals(
                    readStringList(
                            analysis.getRiskSignals()
                    )
            );


            response.setNextSessionAdvice(
                    readStringList(
                            analysis.getNextSessionAdvice()
                    )
            );


            /*
             * 兼容v1历史数据。
             *
             * v1可能没有planAdjustments。
             */
            /*
             * 兼容v1历史数据。
             *
             * v1可能没有planAdjustments。
             */
            if (analysis.getPlanAdjustments() != null
                    && !analysis.getPlanAdjustments().isBlank()) {


                response.setPlanAdjustments(

                        objectMapper.readValue(

                                analysis.getPlanAdjustments(),

                                new TypeReference<
                                        List<PlanAdjustmentProposalVO>
                                        >() {
                                }
                        )
                );


            } else {


                response.setPlanAdjustments(
                        Collections.emptyList()
                );

            }


            /*
             * Nutrition Analysis
             *
             * 不依赖planAdjustments
             */
            if (analysis.getNutritionAnalysis() != null
                    && !analysis.getNutritionAnalysis().isBlank()) {


                response.setNutritionAnalysis(
                        objectMapper.readValue(
                                analysis.getNutritionAnalysis(),
                                NutritionAnalysisVO.class
                        )
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
     * 8. 安全读取数据库里的字符串数组JSON
     * =========================================================
     */
    private List<String> readStringList(
            String json
    ) throws JsonProcessingException {


        if (json == null
                || json.isBlank()) {


            return Collections.emptyList();
        }


        return objectMapper.readValue(

                json,

                new TypeReference<List<String>>() {
                }
        );
    }


    /*
     * =========================================================
     * 9. 验证AI Coach返回
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


        /*
         * Score必须0-100
         */
        if (response.getScore() == null
                || response.getScore() < 0
                || response.getScore() > 100) {


            throw new BusinessException(
                    HttpStatus.BAD_GATEWAY,
                    "AI返回评分格式异常"
            );
        }


        /*
         * Summary不能为空
         */
        if (response.getSummary() == null
                || response.getSummary().isBlank()) {


            throw new BusinessException(
                    HttpStatus.BAD_GATEWAY,
                    "AI返回总结为空"
            );
        }


        /*
         * v2/v3必须返回计划调整建议数组
         */
        if (response.getPlanAdjustments() == null) {


            throw new BusinessException(
                    HttpStatus.BAD_GATEWAY,
                    "AI未返回训练计划调整建议"
            );
        }
    }


    /*
     * =========================================================
     * 10. 用户确认后应用AI训练调整
     * =========================================================
     */
    @Transactional
    public void applyPlanAdjustments(
            Long analysisId
    ) {


        /*
         * 1. 查询AI分析
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
         * 2. 防止重复应用
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
         * 3. 查询原训练Session
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
         * 自由训练没有planDayId。
         */
        if (session.getPlanDayId() == null) {


            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "该训练不是计划训练，无法应用计划调整"
            );
        }


        /*
         * 防止历史旧分析没有planAdjustments。
         */
        if (analysis.getPlanAdjustments() == null
                || analysis.getPlanAdjustments().isBlank()) {


            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "该AI分析没有可应用的训练调整"
            );
        }


        try {


            /*
             * 4. JSON → Proposal
             */
            List<PlanAdjustmentProposalVO> proposals =
                    objectMapper.readValue(

                            analysis.getPlanAdjustments(),

                            new TypeReference<
                                    List<PlanAdjustmentProposalVO>
                                    >() {
                            }
                    );


            if (proposals.isEmpty()) {


                throw new BusinessException(
                        HttpStatus.BAD_REQUEST,
                        "该AI分析没有可应用的训练调整"
                );
            }


            /*
             * 5. 查询数据库真实计划
             */
            List<TrainingPlanExerciseVO> currentExercises =
                    trainingPlanExerciseMapper.findByPlanDayId(
                            session.getPlanDayId()
                    );


            /*
             * 6. 一条一条验证
             */
            for (PlanAdjustmentProposalVO proposal
                    : proposals) {


                if (proposal == null
                        || proposal.getExerciseId() == null) {


                    throw new BusinessException(
                            HttpStatus.BAD_REQUEST,
                            "AI训练调整建议缺少动作信息"
                    );
                }


                TrainingPlanExerciseVO current =
                        currentExercises
                                .stream()
                                .filter(
                                        exercise ->
                                                exercise
                                                        .getExerciseId()
                                                        .equals(
                                                                proposal.getExerciseId()
                                                        )
                                )
                                .findFirst()
                                .orElseThrow(
                                        () ->
                                                new BusinessException(
                                                        HttpStatus.BAD_REQUEST,
                                                        "AI建议包含当前计划中不存在的动作"
                                                )
                                );


                /*
                 * 7. 防止旧Proposal覆盖用户后来修改的计划。
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
                 * 8. Java业务校验
                 */
                validatePlanAdjustment(
                        proposal
                );


                /*
                 * 9. 真正UPDATE计划
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
             * 10. 整批更新成功后
             * 才标记为已应用。
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


    /*
     * =========================================================
     * 11. BigDecimal重量比较
     * =========================================================
     */
    private boolean sameWeight(
            BigDecimal first,
            BigDecimal second
    ) {


        if (first == null
                && second == null) {


            return true;
        }


        if (first == null
                || second == null) {


            return false;
        }


        /*
         * 60.0
         * 和
         * 60.00
         *
         * 数值应视为相等。
         */
        return first.compareTo(
                second
        ) == 0;
    }


    /*
     * =========================================================
     * 12. AI Plan Adjustment基础业务校验
     * =========================================================
     */
    private void validatePlanAdjustment(
            PlanAdjustmentProposalVO proposal
    ) {


        if (proposal == null) {


            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "AI返回了空的训练调整建议"
            );
        }


        List<String> allowedActions =
                List.of(
                        "KEEP",
                        "INCREASE",
                        "DECREASE",
                        "ADJUST"
                );


        /*
         * action是否合法
         */
        if (proposal.getAction() == null
                || !allowedActions.contains(
                proposal.getAction()
        )) {


            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "AI返回了非法训练调整类型"
            );
        }


        /*
         * exerciseId不能为空
         */
        if (proposal.getExerciseId() == null) {


            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "AI训练调整缺少exerciseId"
            );
        }


        /*
         * 组数必须 > 0
         */
        if (proposal.getTargetSets() == null
                || proposal.getTargetSets() <= 0) {


            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "AI返回了非法目标组数"
            );
        }


        /*
         * reps范围是否合法
         */
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


        /*
         * 重量不能为负数
         */
        if (proposal.getRecommendedWeight() != null
                && proposal
                .getRecommendedWeight()
                .compareTo(
                        BigDecimal.ZERO
                ) < 0) {


            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "AI返回了非法训练重量"
            );
        }
    }
}