package com.smartfit.backend.controller;

import com.smartfit.backend.common.Result;
import com.smartfit.backend.dto.TrainingSessionCreateRequest;
import com.smartfit.backend.entity.TrainingSession;
import com.smartfit.backend.service.TrainingService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import com.smartfit.backend.vo.TrainingSessionDetailVO;
import com.smartfit.backend.vo.TrainingSessionSummaryVO;

@RestController
@RequestMapping("/api")
public class TrainingController {

    private final TrainingService trainingService;


    public TrainingController(TrainingService trainingService) {
        this.trainingService = trainingService;
    }


    @PostMapping("/users/{userId}/training-sessions")
    public Result<TrainingSession> createTrainingSession(
            @PathVariable Long userId,
            @Valid @RequestBody TrainingSessionCreateRequest request
    ) {

        TrainingSession session =
                trainingService.createSession(userId, request);

        return Result.success(session);
    }

    @GetMapping("/training-sessions/{sessionId}")
    public Result<TrainingSessionDetailVO> getTrainingSessionDetail(
            @PathVariable Long sessionId
    ) {

        TrainingSessionDetailVO detail =
                trainingService.getSessionDetail(sessionId);

        return Result.success(detail);
    }
    @GetMapping("/training-sessions/{sessionId}/summary")
    public Result<TrainingSessionSummaryVO> getTrainingSessionSummary(
            @PathVariable Long sessionId
    ) {

        TrainingSessionSummaryVO summary =
                trainingService.getSessionSummary(sessionId);

        return Result.success(summary);
    }
}