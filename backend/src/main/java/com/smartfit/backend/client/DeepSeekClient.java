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
import java.util.List;
import java.util.Map;

@Component
public class DeepSeekClient {

    private final RestClient restClient;

    private final String apiKey;
    private final String model;


    public DeepSeekClient(
            @Value("${deepseek.base-url}") String baseUrl,
            @Value("${deepseek.api-key}") String apiKey,
            @Value("${deepseek.model}") String model
    ) {
        this.apiKey = apiKey;
        this.model = model;

        this.restClient =
                RestClient.builder()
                        .baseUrl(baseUrl)
                        .build();
    }


    public String generateJson(
            String systemPrompt,
            String userPrompt
    ) {

        if (apiKey == null || apiKey.isBlank()) {
            throw new BusinessException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "DeepSeek API Key未配置"
            );
        }


        for (int attempt = 1; attempt <= 2; attempt++) {

            Map<String, Object> requestBody =
                    Map.of(
                            "model", model,

                            "messages", List.of(
                                    Map.of(
                                            "role", "system",
                                            "content", systemPrompt
                                    ),
                                    Map.of(
                                            "role", "user",
                                            "content",
                                            userPrompt
                                                    + "\n\n请务必返回一个非空、完整、合法的 JSON 对象。"
                                    )
                            ),

                            "thinking", Map.of(
                                    "type", "disabled"
                            ),

                            "response_format", Map.of(
                                    "type", "json_object"
                            ),

                            "max_tokens", 2000
                    );


            try {

                JsonNode response =
                        restClient
                                .post()
                                .uri("/chat/completions")
                                .header(
                                        HttpHeaders.AUTHORIZATION,
                                        "Bearer " + apiKey
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .body(requestBody)
                                .retrieve()
                                .body(JsonNode.class);


                if (response == null) {

                    if (attempt == 2) {
                        throw new BusinessException(
                                HttpStatus.BAD_GATEWAY,
                                "AI服务没有返回结果"
                        );
                    }

                    continue;
                }


                JsonNode contentNode =
                        response.path("choices")
                                .path(0)
                                .path("message")
                                .path("content");


                if (!contentNode.isMissingNode()
                        && !contentNode.isNull()
                        && !contentNode.asText().isBlank()) {

                    return contentNode.asText();
                }


                // 第一次为空 → 再尝试一次
                if (attempt == 2) {
                    throw new BusinessException(
                            HttpStatus.BAD_GATEWAY,
                            "AI连续两次返回空内容"
                    );
                }


            } catch (BusinessException e) {

                throw e;

            } catch (Exception e) {

                throw new BusinessException(
                        HttpStatus.BAD_GATEWAY,
                        "调用AI服务失败"
                );
            }
        }


        throw new BusinessException(
                HttpStatus.BAD_GATEWAY,
                "AI服务调用失败"
        );
    }
    public JsonNode chatWithTools(
            List<Map<String, Object>> messages,
            List<Map<String, Object>> tools
    ) {

        if (apiKey == null || apiKey.isBlank()) {
            throw new BusinessException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "DeepSeek API Key未配置"
            );
        }


        Map<String, Object> requestBody =
                Map.of(
                        "model", model,
                        "messages", messages,
                        "tools", tools,
                        "tool_choice", "auto",

                        // Day8第一版先关闭thinking，
                        // 简化多轮Tool Calling
                        "thinking", Map.of(
                                "type", "disabled"
                        ),

                        "max_tokens", 2000
                );


        try {

            JsonNode response =
                    restClient
                            .post()
                            .uri("/chat/completions")
                            .header(
                                    HttpHeaders.AUTHORIZATION,
                                    "Bearer " + apiKey
                            )
                            .contentType(
                                    MediaType.APPLICATION_JSON
                            )
                            .body(requestBody)
                            .retrieve()
                            .body(JsonNode.class);


            if (response == null) {
                throw new BusinessException(
                        HttpStatus.BAD_GATEWAY,
                        "DeepSeek没有返回结果"
                );
            }


            return response;


        } catch (BusinessException e) {

            throw e;

        } catch (RestClientResponseException e) {

            String responseBody =
                    e.getResponseBodyAsString();

            if (responseBody != null
                    && responseBody.length() > 800) {

                responseBody =
                        responseBody.substring(0, 800);
            }

            throw new BusinessException(
                    HttpStatus.BAD_GATEWAY,
                    "DeepSeek Tool Calling失败，远程HTTP状态="
                            + e.getStatusCode().value()
                            + "，响应="
                            + responseBody
            );

        } catch (Exception e) {

            throw new BusinessException(
                    HttpStatus.BAD_GATEWAY,
                    "DeepSeek Tool Calling本地异常："
                            + e.getClass().getSimpleName()
                            + " - "
                            + e.getMessage()
            );
        }
    }
    public JsonNode chatWithoutTools(
            List<Map<String, Object>> messages
    ) {

        if (apiKey == null || apiKey.isBlank()) {
            throw new BusinessException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "DeepSeek API Key未配置"
            );
        }


        Map<String, Object> requestBody =
                Map.of(
                        "model", model,
                        "messages", messages,

                        "thinking", Map.of(
                                "type", "disabled"
                        ),

                        "max_tokens", 2500
                );


        try {

            JsonNode response =
                    restClient
                            .post()
                            .uri("/chat/completions")
                            .header(
                                    HttpHeaders.AUTHORIZATION,
                                    "Bearer " + apiKey
                            )
                            .contentType(
                                    MediaType.APPLICATION_JSON
                            )
                            .body(requestBody)
                            .retrieve()
                            .body(JsonNode.class);


            if (response == null) {
                throw new BusinessException(
                        HttpStatus.BAD_GATEWAY,
                        "DeepSeek没有返回最终答案"
                );
            }


            return response;


        } catch (BusinessException e) {

            throw e;

        } catch (RestClientResponseException e) {

            String responseBody =
                    e.getResponseBodyAsString();

            throw new BusinessException(
                    HttpStatus.BAD_GATEWAY,
                    "DeepSeek最终总结失败，远程HTTP状态="
                            + e.getStatusCode().value()
                            + "，响应="
                            + responseBody
            );

        } catch (Exception e) {

            throw new BusinessException(
                    HttpStatus.BAD_GATEWAY,
                    "DeepSeek最终总结本地异常："
                            + e.getClass().getSimpleName()
                            + " - "
                            + e.getMessage()
            );
        }
    }
    public JsonNode chatWithoutToolsJson(
            List<Map<String, Object>> messages
    ) {

        if (apiKey == null || apiKey.isBlank()) {
            throw new BusinessException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "DeepSeek API Key未配置"
            );
        }


        Map<String, Object> requestBody =
                Map.of(
                        "model", model,

                        "messages", messages,

                        "thinking", Map.of(
                                "type", "disabled"
                        ),

                        "response_format", Map.of(
                                "type", "json_object"
                        ),

                        "max_tokens", 2500
                );


        try {

            JsonNode response =
                    restClient
                            .post()
                            .uri("/chat/completions")
                            .header(
                                    HttpHeaders.AUTHORIZATION,
                                    "Bearer " + apiKey
                            )
                            .contentType(
                                    MediaType.APPLICATION_JSON
                            )
                            .body(requestBody)
                            .retrieve()
                            .body(JsonNode.class);


            if (response == null) {
                throw new BusinessException(
                        HttpStatus.BAD_GATEWAY,
                        "DeepSeek没有返回Evidence JSON"
                );
            }


            return response;


        } catch (BusinessException e) {

            throw e;

        } catch (RestClientResponseException e) {

            throw new BusinessException(
                    HttpStatus.BAD_GATEWAY,
                    "DeepSeek Evidence JSON调用失败，远程HTTP状态="
                            + e.getStatusCode().value()
                            + "，响应="
                            + e.getResponseBodyAsString()
            );

        } catch (Exception e) {

            throw new BusinessException(
                    HttpStatus.BAD_GATEWAY,
                    "DeepSeek Evidence JSON本地异常："
                            + e.getClass().getSimpleName()
                            + " - "
                            + e.getMessage()
            );
        }
    }
}