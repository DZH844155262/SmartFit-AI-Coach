package com.smartfit.backend.dto;

import jakarta.validation.constraints.NotNull;

public class TrainingSessionStartRequest {

    @NotNull(message = "planDayId不能为空")
    private Long planDayId;

    @NotNull(message = "exerciseId不能为空")
    private Long exerciseId;


    public Long getPlanDayId() {
        return planDayId;
    }

    public void setPlanDayId(Long planDayId) {
        this.planDayId = planDayId;
    }

    public Long getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(Long exerciseId) {
        this.exerciseId = exerciseId;
    }
}