package com.smartfit.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public class TrainingPlanDayRequest {

    @NotBlank(message = "训练日名称不能为空")
    private String title;

    private String notes;

    @Valid
    @NotEmpty(message = "训练日必须至少包含一个动作")
    private List<TrainingPlanExerciseRequest> exercises;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<TrainingPlanExerciseRequest> getExercises() {
        return exercises;
    }

    public void setExercises(List<TrainingPlanExerciseRequest> exercises) {
        this.exercises = exercises;
    }
}