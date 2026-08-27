package com.smartfit.backend.controller;

import com.smartfit.backend.common.Result;
import com.smartfit.backend.dto.TrainingPlanCreateRequest;
import com.smartfit.backend.entity.TrainingPlan;
import com.smartfit.backend.service.TrainingPlanService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import com.smartfit.backend.vo.TrainingPlanDetailVO;

@RestController
@RequestMapping("/api")
public class TrainingPlanController {

    private final TrainingPlanService trainingPlanService;


    public TrainingPlanController(
            TrainingPlanService trainingPlanService
    ) {
        this.trainingPlanService = trainingPlanService;
    }


    @PostMapping("/users/{userId}/training-plans")
    public Result<TrainingPlan> createTrainingPlan(
            @PathVariable Long userId,
            @Valid @RequestBody TrainingPlanCreateRequest request
    ) {

        TrainingPlan trainingPlan =
                trainingPlanService.createPlan(
                        userId,
                        request
                );

        return Result.success(trainingPlan);
    }
    @GetMapping("/training-plans/{planId}")
    public Result<TrainingPlanDetailVO> getTrainingPlanDetail(
            @PathVariable Long planId
    ) {

        TrainingPlanDetailVO detail =
                trainingPlanService.getPlanDetail(planId);

        return Result.success(detail);
    }
}