package com.smartfit.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public class TrainingSessionCreateRequest {

    @NotNull(message = "训练日期不能为空")
    private LocalDate sessionDate;

    private String title;

    private String notes;

    @Valid
    @NotEmpty(message = "一次训练至少需要包含一个动作")
    private List<TrainingExerciseRequest> exercises;


    public LocalDate getSessionDate() {
        return sessionDate;
    }

    public void setSessionDate(LocalDate sessionDate) {
        this.sessionDate = sessionDate;
    }

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

    public List<TrainingExerciseRequest> getExercises() {
        return exercises;
    }

    public void setExercises(List<TrainingExerciseRequest> exercises) {
        this.exercises = exercises;
    }
}