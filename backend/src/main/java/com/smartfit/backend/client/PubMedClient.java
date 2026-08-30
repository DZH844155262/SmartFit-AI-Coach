package com.smartfit.backend.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.smartfit.backend.exception.BusinessException;
import com.smartfit.backend.vo.PubMedPaperVO;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class PubMedClient {

    private final RestClient restClient;


    public PubMedClient() {

        this.restClient =
                RestClient.builder()
                        .baseUrl(
                                "https://eutils.ncbi.nlm.nih.gov"
                        )
                        .build();
    }


    /*
     * 第一步：
     * 根据关键词搜索PubMed，
     * 得到PMID。
     */
    public List<String> searchPaperIds(
            String query,
            int limit
    ) {

        if (query == null || query.isBlank()) {
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "论文搜索关键词不能为空"
            );
        }


        int safeLimit =
                Math.min(
                        Math.max(limit, 1),
                        10
                );


        try {

            JsonNode response =
                    restClient
                            .get()
                            .uri(uriBuilder ->
                                    uriBuilder
                                            .path(
                                                    "/entrez/eutils/esearch.fcgi"
                                            )
                                            .queryParam(
                                                    "db",
                                                    "pubmed"
                                            )
                                            .queryParam(
                                                    "term",
                                                    query
                                            )
                                            .queryParam(
                                                    "retmax",
                                                    safeLimit
                                            )
                                            .queryParam(
                                                    "retmode",
                                                    "json"
                                            )
                                            .queryParam(
                                                    "sort",
                                                    "relevance"
                                            )
                                            .build()
                            )
                            .retrieve()
                            .body(JsonNode.class);


            if (response == null) {
                throw new BusinessException(
                        HttpStatus.BAD_GATEWAY,
                        "PubMed没有返回搜索结果"
                );
            }


            JsonNode idList =
                    response
                            .path("esearchresult")
                            .path("idlist");


            List<String> ids =
                    new ArrayList<>();


            if (idList.isArray()) {

                for (JsonNode idNode : idList) {

                    ids.add(
                            idNode.asText()
                    );
                }
            }


            return ids;


        } catch (BusinessException e) {

            throw e;

        } catch (Exception e) {

            throw new BusinessException(
                    HttpStatus.BAD_GATEWAY,
                    "调用PubMed搜索失败"
            );
        }
    }


    /*
     * 第二步：
     * 搜到PMID后，
     * 再去PubMed获取论文详细信息。
     */
    public List<PubMedPaperVO> searchPapers(
            String query,
            int limit
    ) {

        List<String> ids =
                searchPaperIds(
                        query,
                        limit
                );


        if (ids.isEmpty()) {
            return new ArrayList<>();
        }


        return fetchPapers(ids);
    }


    /*
     * 根据PMID批量获取论文：
     *
     * 标题
     * 期刊
     * 年份
     * 摘要
     */
    private List<PubMedPaperVO> fetchPapers(
            List<String> ids
    ) {

        try {

            String joinedIds =
                    String.join(
                            ",",
                            ids
                    );


            String xml =
                    restClient
                            .get()
                            .uri(uriBuilder ->
                                    uriBuilder
                                            .path(
                                                    "/entrez/eutils/efetch.fcgi"
                                            )
                                            .queryParam(
                                                    "db",
                                                    "pubmed"
                                            )
                                            .queryParam(
                                                    "id",
                                                    joinedIds
                                            )
                                            .queryParam(
                                                    "retmode",
                                                    "xml"
                                            )
                                            .build()
                            )
                            .retrieve()
                            .body(String.class);


            if (xml == null || xml.isBlank()) {
                throw new BusinessException(
                        HttpStatus.BAD_GATEWAY,
                        "PubMed论文详情为空"
                );
            }


            return parsePubMedXml(
                    xml
            );


        } catch (BusinessException e) {

            throw e;

        } catch (Exception e) {

            throw new BusinessException(
                    HttpStatus.BAD_GATEWAY,
                    "获取PubMed论文详情失败"
            );
        }
    }


    /*
     * 第三步：
     * 把PubMed返回的XML
     * 解析成Java对象。
     */
    private List<PubMedPaperVO> parsePubMedXml(
            String xml
    ) throws Exception {

        DocumentBuilderFactory factory =
                DocumentBuilderFactory.newInstance();

        /*
         * PubMed XML可能包含DOCTYPE，
         * 所以允许DOCTYPE存在，
         * 但禁止Java从外部加载DTD和外部实体。
         */
        factory.setFeature(
                "http://xml.org/sax/features/external-general-entities",
                false
        );

        factory.setFeature(
                "http://xml.org/sax/features/external-parameter-entities",
                false
        );

        factory.setFeature(
                "http://apache.org/xml/features/nonvalidating/load-external-dtd",
                false
        );

        factory.setXIncludeAware(false);

        factory.setExpandEntityReferences(false);


        Document document =
                factory
                        .newDocumentBuilder()
                        .parse(
                                new ByteArrayInputStream(
                                        xml.getBytes(
                                                StandardCharsets.UTF_8
                                        )
                                )
                        );


        NodeList articleNodes =
                document.getElementsByTagName(
                        "PubmedArticle"
                );


        List<PubMedPaperVO> papers =
                new ArrayList<>();


        for (int i = 0;
             i < articleNodes.getLength();
             i++) {

            Node node =
                    articleNodes.item(i);


            if (!(node instanceof Element article)) {
                continue;
            }


            PubMedPaperVO paper =
                    new PubMedPaperVO();


            paper.setPmid(
                    firstText(
                            article,
                            "PMID"
                    )
            );


            paper.setTitle(
                    firstText(
                            article,
                            "ArticleTitle"
                    )
            );


            paper.setJournal(
                    firstText(
                            article,
                            "Title"
                    )
            );


            String year =
                    firstText(
                            article,
                            "Year"
                    );


            if (year == null || year.isBlank()) {

                year =
                        firstText(
                                article,
                                "MedlineDate"
                        );
            }


            paper.setYear(
                    year
            );


            paper.setAbstractText(
                    readAbstract(
                            article
                    )
            );


            papers.add(
                    paper
            );
        }


        return papers;
    }


    /*
     * 读取某个XML标签的第一条文字。
     */
    private String firstText(
            Element element,
            String tagName
    ) {

        NodeList nodes =
                element.getElementsByTagName(
                        tagName
                );


        if (nodes.getLength() == 0) {
            return null;
        }


        String text =
                nodes
                        .item(0)
                        .getTextContent();


        return text == null
                ? null
                : text.trim();
    }


    /*
     * PubMed的一篇论文可能有多个AbstractText，
     * 比如：
     *
     * BACKGROUND
     * METHODS
     * RESULTS
     * CONCLUSION
     *
     * 所以我们全部拼起来。
     */
    private String readAbstract(
            Element article
    ) {

        NodeList abstractNodes =
                article.getElementsByTagName(
                        "AbstractText"
                );


        if (abstractNodes.getLength() == 0) {
            return null;
        }


        StringBuilder builder =
                new StringBuilder();


        for (int i = 0;
             i < abstractNodes.getLength();
             i++) {

            Node node =
                    abstractNodes.item(i);


            if (!(node instanceof Element abstractElement)) {
                continue;
            }


            String label =
                    abstractElement.getAttribute(
                            "Label"
                    );


            String text =
                    abstractElement
                            .getTextContent()
                            .trim();


            if (!label.isBlank()) {

                builder.append(
                        label
                );

                builder.append(
                        ": "
                );
            }


            builder.append(
                    text
            );


            if (i
                    < abstractNodes.getLength() - 1) {

                builder.append(
                        "\n"
                );
            }
        }


        return builder.toString();
    }
}