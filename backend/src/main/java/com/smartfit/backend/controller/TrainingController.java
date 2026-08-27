package com.smartfit.backend.controller;

import com.smartfit.backend.common.Result;
import com.smartfit.backend.dto.TrainingSessionCreateRequest;
import com.smartfit.backend.entity.TrainingSession;
import com.smartfit.backend.service.TrainingService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import com.smartfit.backend.vo.TrainingSessionDetailVO;
import com.smartfit.backend.vo.TrainingSessionSummaryVO;
import com.smartfit.backend.vo.TrainingSessionListItemVO;
import com.smartfit.backend.vo.ExerciseHistoryItemVO;
import com.smartfit.backend.vo.ExerciseTrendVO;
import com.smartfit.backend.vo.TrainingPlanComparisonVO;
import java.util.List;

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
    @GetMapping("/users/{userId}/training-sessions")
    public Result<List<TrainingSessionListItemVO>> getRecentTrainingSessions(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") Integer limit
    ) {

        List<TrainingSessionListItemVO> sessions =
                trainingService.getRecentSessions(
                        userId,
                        limit
                );

        return Result.success(sessions);
    }
    @GetMapping("/users/{userId}/exercises/{exerciseId}/history")
    public Result<List<ExerciseHistoryItemVO>> getExerciseHistory(
            @PathVariable Long userId,
            @PathVariable Long exerciseId,
            @RequestParam(defaultValue = "20") Integer limit
    ) {

        List<ExerciseHistoryItemVO> history =
                trainingService.getExerciseHistory(
                        userId,
                        exerciseId,
                        limit
                );

        return Result.success(history);
    }
    @GetMapping("/users/{userId}/exercises/{exerciseId}/trend")
    public Result<ExerciseTrendVO> getExerciseTrend(
            @PathVariable Long userId,
            @PathVariable Long exerciseId,
            @RequestParam(defaultValue = "20") Integer limit
    ) {

        ExerciseTrendVO trend =
                trainingService.getExerciseTrend(
                        userId,
                        exerciseId,
                        limit
                );

        return Result.success(trend);
    }
    @GetMapping("/training-sessions/{sessionId}/plan-comparison")
    public Result<TrainingPlanComparisonVO> getPlanComparison(
            @PathVariable Long sessionId
    ) {

        TrainingPlanComparisonVO comparison =
                trainingService.getPlanComparison(
                        sessionId
                );

        return Result.success(comparison);
    }
}