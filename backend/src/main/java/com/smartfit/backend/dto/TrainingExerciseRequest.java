package com.smartfit.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class TrainingExerciseRequest {

    @NotNull(message = "动作ID不能为空")
    private Long exerciseId;

    private String notes;

    @Valid
    @NotEmpty(message = "一个动作至少需要记录一组训练")
    private List<TrainingSetRequest> sets;


    public Long getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(Long exerciseId) {
        this.exerciseId = exerciseId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<TrainingSetRequest> getSets() {
        return sets;
    }

    public void setSets(List<TrainingSetRequest> sets) {
        this.sets = sets;
    }
}