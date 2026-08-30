package com.smartfit.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartfit.backend.client.DeepSeekClient;
import com.smartfit.backend.client.PubMedClient;
import com.smartfit.backend.exception.BusinessException;
import com.smartfit.backend.vo.EvidenceAgentResponseVO;
import com.smartfit.backend.vo.EvidenceItemVO;
import com.smartfit.backend.vo.PubMedPaperVO;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class EvidenceAgentService {

    private final DeepSeekClient deepSeekClient;
    private final PubMedClient pubMedClient;
    private final ObjectMapper objectMapper;


    public EvidenceAgentService(
            DeepSeekClient deepSeekClient,
            PubMedClient pubMedClient,
            ObjectMapper objectMapper
    ) {
        this.deepSeekClient = deepSeekClient;
        this.pubMedClient = pubMedClient;
        this.objectMapper = objectMapper;
    }


    public EvidenceAgentResponseVO answerWithEvidence(
            String question
    ) {

        /*
         * =====================================================
         * 1. 定义search_papers工具
         * =====================================================
         */
        List<Map<String, Object>> tools =
                List.of(
                        Map.of(
                                "type", "function",

                                "function", Map.of(
                                        "name", "search_papers",

                                        "description",
                                        """
                                        Search PubMed for scientific evidence
                                        related to resistance training,
                                        hypertrophy, strength,
                                        progressive overload,
                                        RPE, RIR and exercise science.

                                        Prefer recent high-quality evidence,
                                        especially position stands,
                                        systematic reviews and meta-analyses.
                                        """,

                                        "parameters", Map.of(
                                                "type", "object",

                                                "properties", Map.of(
                                                        "query", Map.of(
                                                                "type",
                                                                "string",

                                                                "description",
                                                                "PubMed search query in English"
                                                        ),

                                                        "limit", Map.of(
                                                                "type",
                                                                "integer",

                                                                "description",
                                                                "Normally 3 papers"
                                                        )
                                                ),

                                                "required",
                                                List.of("query")
                                        )
                                )
                        )
                );


        /*
         * =====================================================
         * 2. conversation
         * =====================================================
         */
        List<Map<String, Object>> messages =
                new ArrayList<>();


        messages.add(
                Map.of(
                        "role",
                        "system",

                        "content",
                        """
                        You are the evidence component
                        of SmartFit AI Coach.

                        Use PubMed evidence when answering
                        exercise-science questions.

                        Prefer:
                        1. Position stands
                        2. Systematic reviews
                        3. Meta-analyses
                        4. Recent peer-reviewed research

                        Do not invent papers,
                        PMID values or findings.

                        You may request multiple
                        search_papers calls if useful.
                        """
                )
        );


        messages.add(
                Map.of(
                        "role",
                        "user",

                        "content",
                        question
                )
        );


        /*
         * =====================================================
         * 3. 第一轮：
         * DeepSeek决定查什么
         * =====================================================
         */
        JsonNode firstResponse =
                deepSeekClient.chatWithTools(
                        messages,
                        tools
                );


        JsonNode assistantMessage =
                firstResponse
                        .path("choices")
                        .path(0)
                        .path("message");


        JsonNode toolCalls =
                assistantMessage.path(
                        "tool_calls"
                );


        /*
         * 本次实际从PubMed得到的论文。
         *
         * key = PMID
         *
         * LinkedHashMap既能去重，
         * 又能保留搜索顺序。
         */
        Map<String, PubMedPaperVO> retrievedPapers =
                new LinkedHashMap<>();


        /*
         * =====================================================
         * 4. 如果模型要求查论文
         * =====================================================
         */
        if (toolCalls.isArray()
                && !toolCalls.isEmpty()) {


            messages.add(
                    objectMapper.convertValue(
                            assistantMessage,
                            Map.class
                    )
            );


            /*
             * 执行这一轮所有Tool Calls
             */
            for (JsonNode toolCall : toolCalls) {

                String toolCallId =
                        toolCall
                                .path("id")
                                .asText();


                String functionName =
                        toolCall
                                .path("function")
                                .path("name")
                                .asText();


                if (!"search_papers".equals(
                        functionName
                )) {

                    throw new BusinessException(
                            HttpStatus.BAD_GATEWAY,
                            "DeepSeek请求了未知工具："
                                    + functionName
                    );
                }


                String argumentsJson =
                        toolCall
                                .path("function")
                                .path("arguments")
                                .asText();


                try {

                    JsonNode arguments =
                            objectMapper.readTree(
                                    argumentsJson
                            );


                    String query =
                            arguments
                                    .path("query")
                                    .asText();


                    int limit =
                            arguments
                                    .path("limit")
                                    .asInt(3);


                    if (query == null
                            || query.isBlank()) {

                        throw new BusinessException(
                                HttpStatus.BAD_GATEWAY,
                                "AI生成的论文搜索关键词为空"
                        );
                    }


                    int safeLimit =
                            Math.min(
                                    Math.max(limit, 1),
                                    3
                            );


                    /*
                     * Java真正搜索PubMed
                     */
                    List<PubMedPaperVO> papers =
                            pubMedClient.searchPapers(
                                    query,
                                    safeLimit
                            );


                    /*
                     * 保存所有真实检索到的论文。
                     */
                    for (PubMedPaperVO paper : papers) {

                        if (paper.getPmid() != null) {

                            retrievedPapers.put(
                                    paper.getPmid(),
                                    paper
                            );
                        }
                    }


                    /*
                     * 返回Tool Result给DeepSeek
                     */
                    String paperJson =
                            objectMapper.writeValueAsString(
                                    papers
                            );


                    messages.add(
                            Map.of(
                                    "role",
                                    "tool",

                                    "tool_call_id",
                                    toolCallId,

                                    "content",
                                    paperJson
                            )
                    );


                } catch (JsonProcessingException e) {

                    throw new BusinessException(
                            HttpStatus.BAD_GATEWAY,
                            "Evidence Tool参数处理失败"
                    );
                }
            }
        }


        /*
         * =====================================================
         * 5. 第二阶段：
         *
         * 禁止继续搜索。
         * 要求输出结构化JSON。
         * =====================================================
         */
        messages.add(
                Map.of(
                        "role",
                        "user",

                        "content",
                        """
                        Evidence gathering is now finished.

                        Do NOT search for more papers.

                        Based only on the PubMed evidence
                        already provided, return a valid JSON object.

                        The word JSON is important:
                        return JSON only.
                        Do not use Markdown code fences.

                        Required format:

                        {
                          "directConclusion": "Direct answer to the question",

                          "keyFindings": [
                            "Scientific finding 1",
                            "Scientific finding 2"
                          ],

                          "limitations": [
                            "Important limitation or uncertainty"
                          ],

                          "evidence": [
                            {
                              "pmid": "PMID from the retrieved papers",
                              "claim": "What this paper supports for the current question"
                            }
                          ]
                        }

                        Rules:

                        1. evidence[].pmid MUST come from the
                           PubMed papers supplied by the tool.

                        2. Never invent PMID values.

                        3. Only include papers that are genuinely
                           relevant to the final conclusion.

                        4. Do not return title, journal or year.
                           The Java backend will obtain those
                           fields from the real PubMed records.

                        5. If evidence is weak or indirect,
                           explicitly state this in limitations.
                        
                        6. Every important claim in keyFindings must be supported
                           by at least one paper included in evidence.
                        
                        7. If a finding cannot be tied to a retrieved PMID,
                           do not include that finding.
                        
                        8. Prefer 2 to 4 highly relevant papers rather than
                           citing only one paper for several broad conclusions.
                        
                        9. evidence[].claim must describe only what that specific
                           paper supports. Do not attribute conclusions from other
                           papers to it.
                        
                        10. directConclusion must be consistent with the evidence
                            included in the final response.
                        """
                )
        );


        /*
         * =====================================================
         * 6. 强制JSON输出
         * =====================================================
         */
        JsonNode finalResponse =
                deepSeekClient.chatWithoutToolsJson(
                        messages
                );


        String json =
                finalResponse
                        .path("choices")
                        .path(0)
                        .path("message")
                        .path("content")
                        .asText();


        if (json == null
                || json.isBlank()) {

            throw new BusinessException(
                    HttpStatus.BAD_GATEWAY,
                    "Evidence Agent最终JSON为空"
            );
        }


        try {

            /*
             * =================================================
             * 7. AI JSON → Java对象
             * =================================================
             */
            EvidenceAgentResponseVO result =
                    objectMapper.readValue(
                            json,
                            EvidenceAgentResponseVO.class
                    );


            validateEvidenceResponse(
                    result,
                    retrievedPapers
            );


            return result;


        } catch (JsonProcessingException e) {

            throw new BusinessException(
                    HttpStatus.BAD_GATEWAY,
                    "Evidence Agent JSON解析失败"
            );
        }
    }


    /*
     * =========================================================
     * 8. Java验证Evidence
     * =========================================================
     */
    private void validateEvidenceResponse(
            EvidenceAgentResponseVO response,
            Map<String, PubMedPaperVO> retrievedPapers
    ) {

        if (response == null
                || response.getDirectConclusion() == null
                || response.getDirectConclusion().isBlank()) {

            throw new BusinessException(
                    HttpStatus.BAD_GATEWAY,
                    "Evidence Agent没有返回有效结论"
            );
        }


        if (response.getEvidence() == null) {

            response.setEvidence(
                    new ArrayList<>()
            );

            return;
        }


        /*
         * AI只能引用Java真实检索过的PMID。
         */
        for (EvidenceItemVO item
                : response.getEvidence()) {

            PubMedPaperVO realPaper =
                    retrievedPapers.get(
                            item.getPmid()
                    );


            if (realPaper == null) {

                throw new BusinessException(
                        HttpStatus.BAD_GATEWAY,
                        "Evidence Agent引用了未检索到的PMID："
                                + item.getPmid()
                );
            }


            /*
             * 标题、期刊、年份全部由Java
             * 使用PubMed真实记录覆盖。
             *
             * 不信任LLM生成这些metadata。
             */
            item.setTitle(
                    realPaper.getTitle()
            );

            item.setJournal(
                    realPaper.getJournal()
            );

            item.setYear(
                    realPaper.getYear()
            );
        }
    }
}