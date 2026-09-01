package com.smartfit.backend.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.smartfit.backend.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.Base64;
import java.util.List;
import java.util.Map;

@Component
public class DeepSeekVisionClient {

    private final RestClient restClient;

    private final String apiKey;

    private final String visionModel;


    public DeepSeekVisionClient(
            @Value("${deepseek.base-url}") String baseUrl,
            @Value("${deepseek.api-key}") String apiKey,
            @Value("${deepseek.vision-model}") String visionModel
    ) {

        this.apiKey = apiKey;
        this.visionModel = visionModel;

        this.restClient =
                RestClient.builder()
                        .baseUrl(baseUrl)
                        .build();
    }


    public String recognizeEquipment(
            byte[] imageBytes,
            String mimeType
    ) {

        if (apiKey == null || apiKey.isBlank()) {

            throw new BusinessException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "DeepSeek API Key未配置"
            );
        }


        if (imageBytes == null
                || imageBytes.length == 0) {

            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "图片不能为空"
            );
        }


        String safeMimeType =
                normalizeMimeType(
                        mimeType
                );


        String base64 =
                Base64
                        .getEncoder()
                        .encodeToString(
                                imageBytes
                        );


        String dataUrl =
                "data:"
                        + safeMimeType
                        + ";base64,"
                        + base64;


        String prompt = """
                请识别图片中的主要健身器械。

                只返回合法JSON，不要返回Markdown。

                返回格式必须是：

                {
                  "equipmentName": "器械中文名称",
                  "equipmentCategory": "器械类别",
                  "possibleExercises": [
                    "可执行动作1"
                  ],
                  "targetMuscles": [
                    "主要目标肌群1"
                  ],
                  "usage": "简洁说明这个器械如何使用",
                  "safetyNotes": [
                    "注意事项1"
                  ],
                  "recognitionConfidence": "HIGH"
                }

                必须遵守：

                1. recognitionConfidence只能是：
                   HIGH
                   MEDIUM
                   LOW

                2. 如果图片不足以可靠识别，
                   recognitionConfidence必须为LOW。

                3. 不要猜测具体品牌或型号，
                   除非图片中有清晰证据。

                4. possibleExercises只返回这个器械
                   合理支持的常见训练动作。

                5. 不进行医疗诊断。

                6. safetyNotes只给一般训练安全提示。

                7. 如果图片不是健身器械，
                   equipmentName填写UNKNOWN，
                   recognitionConfidence填写LOW。
                """;


        Map<String, Object> requestBody =
                Map.of(
                        "model",
                        visionModel,

                        "messages",
                        List.of(
                                Map.of(
                                        "role",
                                        "user",

                                        "content",
                                        List.of(
                                                Map.of(
                                                        "type",
                                                        "text",

                                                        "text",
                                                        prompt
                                                ),

                                                Map.of(
                                                        "type",
                                                        "image_url",

                                                        "image_url",
                                                        Map.of(
                                                                "url",
                                                                dataUrl
                                                        )
                                                )
                                        )
                                )
                        ),

                        "thinking",
                        Map.of(
                                "type",
                                "disabled"
                        ),

                        "response_format",
                        Map.of(
                                "type",
                                "json_object"
                        ),

                        "max_tokens",
                        1200
                );


        try {

            JsonNode response =
                    restClient
                            .post()
                            .uri(
                                    "/chat/completions"
                            )
                            .header(
                                    HttpHeaders.AUTHORIZATION,
                                    "Bearer " + apiKey
                            )
                            .contentType(
                                    MediaType.APPLICATION_JSON
                            )
                            .body(
                                    requestBody
                            )
                            .retrieve()
                            .body(
                                    JsonNode.class
                            );


            if (response == null) {

                throw new BusinessException(
                        HttpStatus.BAD_GATEWAY,
                        "视觉模型没有返回结果"
                );
            }


            String content =
                    response
                            .path("choices")
                            .path(0)
                            .path("message")
                            .path("content")
                            .asText();


            if (content == null
                    || content.isBlank()) {

                throw new BusinessException(
                        HttpStatus.BAD_GATEWAY,
                        "视觉模型返回内容为空"
                );
            }


            return content;


        } catch (BusinessException e) {

            throw e;

        } catch (RestClientResponseException e) {

            throw new BusinessException(
                    HttpStatus.BAD_GATEWAY,
                    "调用视觉模型失败，远程HTTP状态="
                            + e.getStatusCode().value()
                            + "，响应="
                            + e.getResponseBodyAsString()
            );

        } catch (Exception e) {

            throw new BusinessException(
                    HttpStatus.BAD_GATEWAY,
                    "调用视觉模型本地异常："
                            + e.getClass()
                            .getSimpleName()
                            + " - "
                            + e.getMessage()
            );
        }
    }


    private String normalizeMimeType(
            String mimeType
    ) {

        if (mimeType == null
                || mimeType.isBlank()) {

            return "image/jpeg";
        }


        return switch (mimeType) {

            case "image/jpeg",
                 "image/png",
                 "image/gif",
                 "image/webp"
                    -> mimeType;

            default ->
                    throw new BusinessException(
                            HttpStatus.BAD_REQUEST,
                            "暂不支持该图片格式"
                    );
        };
    }
    public String analyzeImage(
            byte[] imageBytes,
            String mimeType,
            String prompt
    ) {

        if (apiKey == null || apiKey.isBlank()) {

            throw new BusinessException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "DeepSeek API Key未配置"
            );
        }


        if (imageBytes == null
                || imageBytes.length == 0) {

            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "图片不能为空"
            );
        }


        String safeMimeType =
                normalizeMimeType(
                        mimeType
                );


        String base64 =
                Base64
                        .getEncoder()
                        .encodeToString(
                                imageBytes
                        );


        String dataUrl =
                "data:"
                        + safeMimeType
                        + ";base64,"
                        + base64;


        Map<String, Object> requestBody =
                Map.of(
                        "model",
                        visionModel,

                        "messages",
                        List.of(
                                Map.of(
                                        "role",
                                        "user",

                                        "content",
                                        List.of(
                                                Map.of(
                                                        "type",
                                                        "text",

                                                        "text",
                                                        prompt
                                                ),

                                                Map.of(
                                                        "type",
                                                        "image_url",

                                                        "image_url",
                                                        Map.of(
                                                                "url",
                                                                dataUrl
                                                        )
                                                )
                                        )
                                )
                        ),

                        "thinking",
                        Map.of(
                                "type",
                                "disabled"
                        ),

                        "response_format",
                        Map.of(
                                "type",
                                "json_object"
                        ),

                        "max_tokens",
                        1600
                );


        try {

            JsonNode response =
                    restClient
                            .post()
                            .uri(
                                    "/chat/completions"
                            )
                            .header(
                                    HttpHeaders.AUTHORIZATION,
                                    "Bearer " + apiKey
                            )
                            .contentType(
                                    MediaType.APPLICATION_JSON
                            )
                            .body(
                                    requestBody
                            )
                            .retrieve()
                            .body(
                                    JsonNode.class
                            );


            if (response == null) {

                throw new BusinessException(
                        HttpStatus.BAD_GATEWAY,
                        "视觉模型没有返回结果"
                );
            }


            String content =
                    response
                            .path("choices")
                            .path(0)
                            .path("message")
                            .path("content")
                            .asText();


            if (content == null
                    || content.isBlank()) {

                throw new BusinessException(
                        HttpStatus.BAD_GATEWAY,
                        "视觉模型返回内容为空"
                );
            }


            return content;


        } catch (BusinessException e) {

            throw e;

        } catch (RestClientResponseException e) {

            throw new BusinessException(
                    HttpStatus.BAD_GATEWAY,
                    "调用视觉模型失败，远程HTTP状态="
                            + e.getStatusCode().value()
                            + "，响应="
                            + e.getResponseBodyAsString()
            );

        } catch (Exception e) {

            throw new BusinessException(
                    HttpStatus.BAD_GATEWAY,
                    "调用视觉模型本地异常："
                            + e.getClass()
                            .getSimpleName()
                            + " - "
                            + e.getMessage()
            );
        }
    }
}