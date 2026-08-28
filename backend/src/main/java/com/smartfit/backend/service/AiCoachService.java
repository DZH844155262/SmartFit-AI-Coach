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
import com.smartfit.backend.vo.ExerciseTrendVO;
import com.smartfit.backend.vo.PlanExerciseComparisonVO;
import com.smartfit.backend.vo.TrainingPlanComparisonVO;
import com.smartfit.backend.vo.TrainingSessionDetailVO;
import com.smartfit.backend.vo.TrainingSessionSummaryVO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

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
    private static final String PROMPT_VERSION = "v1";


    public AiCoachService(
            TrainingService trainingService,
            FitnessProfileMapper fitnessProfileMapper,
            DeepSeekClient deepSeekClient,
            ObjectMapper objectMapper,
            AiTrainingAnalysisMapper aiTrainingAnalysisMapper,
            @Value("${deepseek.model}") String model
    ) {
        this.trainingService = trainingService;
        this.fitnessProfileMapper = fitnessProfileMapper;
        this.deepSeekClient = deepSeekClient;
        this.objectMapper = objectMapper;
        this.aiTrainingAnalysisMapper = aiTrainingAnalysisMapper;
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
                      ]
                    }
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
    }
}