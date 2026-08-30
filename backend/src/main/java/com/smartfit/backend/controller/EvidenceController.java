package com.smartfit.backend.controller;

import com.smartfit.backend.client.PubMedClient;
import com.smartfit.backend.common.Result;
import com.smartfit.backend.service.EvidenceAgentService;
import com.smartfit.backend.vo.PubMedPaperVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.smartfit.backend.vo.EvidenceAgentResponseVO;
import java.util.List;

@RestController
@RequestMapping("/api/evidence")
public class EvidenceController {

    private final PubMedClient pubMedClient;
    private final EvidenceAgentService evidenceAgentService;


    public EvidenceController(
            PubMedClient pubMedClient,
            EvidenceAgentService evidenceAgentService
    ) {
        this.pubMedClient = pubMedClient;
        this.evidenceAgentService = evidenceAgentService;
    }


    // 原来的：直接测试PubMed搜索
    @GetMapping("/papers/search")
    public Result<List<PubMedPaperVO>> searchPapers(
            @RequestParam String query,
            @RequestParam(defaultValue = "5") int limit
    ) {

        List<PubMedPaperVO> papers =
                pubMedClient.searchPapers(
                        query,
                        limit
                );

        return Result.success(
                papers
        );
    }


    // 新增的：测试DeepSeek + Tool Calling + PubMed
    @GetMapping("/coach-test")
    public Result<EvidenceAgentResponseVO> coachTest(
            @RequestParam String question
    ) {

        EvidenceAgentResponseVO answer =
                evidenceAgentService
                        .answerWithEvidence(
                                question
                        );


        return Result.success(
                answer
        );
    }
}