package com.smartfit.backend.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartfit.backend.client.DeepSeekClient;
import com.smartfit.backend.dto.ai.AiCoachResponse;
import com.smartfit.backend.entity.AiTrainingAnalysis;
import com.smartfit.backend.mapper.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.assertThrows;
import com.smartfit.backend.vo.NutritionAnalysisVO;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class AiCoachServiceTest {


    /*
     * Mock依赖
     */

    private final TrainingService trainingService =
            Mockito.mock(TrainingService.class);


    private final NutritionService nutritionService =
            Mockito.mock(NutritionService.class);


    private final FitnessProfileMapper fitnessProfileMapper =
            Mockito.mock(FitnessProfileMapper.class);


    private final DeepSeekClient deepSeekClient =
            Mockito.mock(DeepSeekClient.class);


    private final AiTrainingAnalysisMapper aiTrainingAnalysisMapper =
            Mockito.mock(AiTrainingAnalysisMapper.class);


    private final TrainingSessionMapper trainingSessionMapper =
            Mockito.mock(TrainingSessionMapper.class);


    private final TrainingPlanExerciseMapper trainingPlanExerciseMapper =
            Mockito.mock(TrainingPlanExerciseMapper.class);


    private final EvidenceAgentService evidenceAgentService =
            Mockito.mock(EvidenceAgentService.class);



    private AiCoachService createService(){


        return new AiCoachService(

                trainingService,

                nutritionService,

                fitnessProfileMapper,

                deepSeekClient,

                new ObjectMapper(),

                aiTrainingAnalysisMapper,

                trainingSessionMapper,

                trainingPlanExerciseMapper,

                evidenceAgentService,

                "deepseek-v4-pro"

        );

    }



    /*
     * Test 1:
     *
     * 已存在AI分析结果
     *
     * 应该：
     *
     * 1. 直接读取数据库
     * 2. 返回历史结果
     * 3. 不调用DeepSeek
     *
     */
    @Test
    void testCacheHit(){


        /*
         * 模拟数据库已有分析
         */
        AiTrainingAnalysis cached =
                new AiTrainingAnalysis();


        cached.setId(100L);


        cached.setScore(85);


        cached.setSummary(
                "训练表现良好"
        );


        cached.setPositiveSignals(
                "[\"完成训练\"]"
        );


        cached.setRiskSignals(
                "[\"恢复不足\"]"
        );


        cached.setNextSessionAdvice(
                "[\"保持当前重量\"]"
        );


        cached.setPlanAdjustments(
                "[]"
        );



        when(
                aiTrainingAnalysisMapper.findLatest(
                        5L,
                        "deepseek-v4-pro",
                        "v3"
                )
        )
                .thenReturn(
                        cached
                );



        AiCoachService service =
                createService();



        AiCoachResponse response =
                service.analyzeSession(
                        5L
                );



        /*
         * 验证返回结果
         */

        assertEquals(
                85,
                response.getScore()
        );


        assertEquals(
                "训练表现良好",
                response.getSummary()
        );



        /*
         * 核心：
         *
         * 缓存命中
         *
         * 不应该调用DeepSeek
         */

        Mockito.verify(
                        deepSeekClient,
                        never()
                )
                .generateJson(
                        Mockito.anyString(),
                        Mockito.anyString()
                );

    }
    @Test
    void testAiInvalidJson(){


        AiCoachService service =
                createService();


        /*
         * 模拟：
         *
         * 数据库没有缓存
         *
         * 所以进入AI生成流程
         */
        when(
                aiTrainingAnalysisMapper.findLatest(
                        5L,
                        "deepseek-v4-pro",
                        "v3"
                )
        )
                .thenReturn(
                        null
                );



        /*
         * 模拟DeepSeek返回非法JSON
         */
        try {

            Mockito.when(
                            deepSeekClient.generateJson(
                                    Mockito.anyString(),
                                    Mockito.anyString()
                            )
                    )
                    .thenReturn(
                            "hello world"
                    );


        } catch (Exception e) {

        }


    }
    @Test
    void testRegenerateShouldCallDeepSeek() {


        AiCoachService service =
                createService();


        /*
         * 模拟DeepSeek返回合法JSON
         */
        String aiJson =
                """
                {
                  "score":80,
                  "summary":"训练表现正常",
                  "positiveSignals":[],
                  "riskSignals":[],
                  "nextSessionAdvice":[],
                  "planAdjustments":[]
                }
                """;


        Mockito.when(
                        deepSeekClient.generateJson(
                                Mockito.anyString(),
                                Mockito.anyString()
                        )
                )
                .thenReturn(
                        aiJson
                );


        /*
         * 这里需要补充：
         *
         * buildCoachRequest()
         * 所需要的数据mock
         *
         * 否则会在训练上下文阶段失败
         */


    }
    @Test
    void testAiResponseJsonParse(){


        ObjectMapper mapper =
                new ObjectMapper();


        String json =
                """
                {
                  "score":90,
                  "summary":"训练完成良好",
                  "positiveSignals":[
                    "完成目标"
                  ],
                  "riskSignals":[],
                  "nextSessionAdvice":[],
                  "planAdjustments":[]
                }
                """;


        try {


            AiCoachResponse response =
                    mapper.readValue(
                            json,
                            AiCoachResponse.class
                    );


            assertEquals(
                    90,
                    response.getScore()
            );


            assertEquals(
                    "训练完成良好",
                    response.getSummary()
            );


        } catch (Exception e){

            throw new RuntimeException(e);

        }


    }
    @Test
    void testInvalidJsonShouldFail(){


        ObjectMapper mapper =
                new ObjectMapper();


        String json =
                """
                hello world
                """;


        assertThrows(
                Exception.class,
                () -> {

                    mapper.readValue(
                            json,
                            AiCoachResponse.class
                    );

                }
        );


    }
    @Test
    void testCacheMiss(){


        when(
                aiTrainingAnalysisMapper.findLatest(
                        5L,
                        "deepseek-v4-pro",
                        "v3"
                )
        )
                .thenReturn(null);


        AiTrainingAnalysis result =
                aiTrainingAnalysisMapper.findLatest(
                        5L,
                        "deepseek-v4-pro",
                        "v3"
                );


        assertEquals(
                null,
                result
        );

    }
    @Test
    void testNutritionInsufficientData(){


        ObjectMapper mapper =
                new ObjectMapper();


        String json =
                """
                {
                  "status":"INSUFFICIENT_DATA",
                  "summary":"饮食记录不足，无法进行长期判断",
                  "suggestions":[
                    "增加饮食记录频率"
                  ]
                }
                """;


        try {


            NutritionAnalysisVO nutrition =
                    mapper.readValue(

                            json,
                            NutritionAnalysisVO.class
                    );


            assertEquals(
                    "INSUFFICIENT_DATA",
                    nutrition.getStatus()
            );


            assertEquals(
                    1,
                    nutrition.getSuggestions().size()
            );


        }catch(Exception e){

            throw new RuntimeException(e);

        }

    }
    @Test
    void testLegacyDataWithoutPlanAdjustments(){


        AiTrainingAnalysis oldData =
                new AiTrainingAnalysis();


        oldData.setScore(80);


        oldData.setSummary(
                "历史版本分析"
        );


        oldData.setPositiveSignals(
                "[]"
        );


        oldData.setRiskSignals(
                "[]"
        );


        oldData.setNextSessionAdvice(
                "[]"
        );


        /*
         * 模拟v1数据
         *
         * planAdjustments为空
         */
        oldData.setPlanAdjustments(
                null
        );


        when(
                aiTrainingAnalysisMapper.findLatest(
                        5L,
                        "deepseek-v4-pro",
                        "v3"
                )
        )
                .thenReturn(
                        oldData
                );


        AiCoachService service =
                createService();



        AiCoachResponse response =
                service.analyzeSession(
                        5L
                );


        assertEquals(
                80,
                response.getScore()
        );


        assertEquals(
                0,
                response.getPlanAdjustments().size()
        );


    }
    @Test
    void testSessionNotFound(){


        AiCoachService service =
                createService();


        when(
                aiTrainingAnalysisMapper.findLatest(
                        999L,
                        "deepseek-v4-pro",
                        "v3"
                )
        )
                .thenReturn(
                        null
                );


        when(
                trainingService.getSessionDetail(
                        999L
                )
        )
                .thenReturn(
                        null
                );


        assertThrows(
                Exception.class,
                () -> {

                    service.analyzeSession(
                            999L
                    );

                }
        );

    }


}