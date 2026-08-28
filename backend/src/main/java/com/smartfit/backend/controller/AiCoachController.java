package com.smartfit.backend.controller;

import com.smartfit.backend.common.Result;
import com.smartfit.backend.dto.ai.AiCoachRequest;
import com.smartfit.backend.dto.ai.AiCoachResponse;
import com.smartfit.backend.service.AiCoachService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AiCoachController {

    private final AiCoachService aiCoachService;

    public AiCoachController(
            AiCoachService aiCoachService
    ) {
        this.aiCoachService = aiCoachService;
    }


    // 开发阶段：查看发送给AI的上下文
    @GetMapping("/training-sessions/{sessionId}/ai-context")
    public Result<AiCoachRequest> getAiContext(
            @PathVariable Long sessionId
    ) {

        AiCoachRequest request =
                aiCoachService.buildCoachRequest(
                        sessionId
                );

        return Result.success(request);
    }


    // 正式：调用AI分析训练
    @PostMapping("/training-sessions/{sessionId}/ai-analysis")
    public Result<AiCoachResponse> analyzeTrainingSession(
            @PathVariable Long sessionId
    ) {

        AiCoachResponse response =
                aiCoachService.analyzeSession(
                        sessionId
                );

        return Result.success(response);
    }
    @PostMapping("/training-sessions/{sessionId}/ai-analysis/regenerate")
    public Result<AiCoachResponse> regenerateTrainingAnalysis(
            @PathVariable Long sessionId
    ) {

        AiCoachResponse response =
                aiCoachService.regenerateSession(
                        sessionId
                );

        return Result.success(response);
    }
}