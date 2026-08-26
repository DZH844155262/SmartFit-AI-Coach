package com.smartfit.backend.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TrainingSet {

    private Long id;
    private Long trainingExerciseId;
    private Integer setNumber;
    private BigDecimal weightKg;
    private Integer reps;
    private BigDecimal rpe;
    private String setType;
    private Boolean completed;
    private LocalDateTime createdTime;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTrainingExerciseId() {
        return trainingExerciseId;
    }

    public void setTrainingExerciseId(Long trainingExerciseId) {
        this.trainingExerciseId = trainingExerciseId;
    }

    public Integer getSetNumber() {
        return setNumber;
    }

    public void setSetNumber(Integer setNumber) {
        this.setNumber = setNumber;
    }

    public BigDecimal getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(BigDecimal weightKg) {
        this.weightKg = weightKg;
    }

    public Integer getReps() {
        return reps;
    }

    public void setReps(Integer reps) {
        this.reps = reps;
    }

    public BigDecimal getRpe() {
        return rpe;
    }

    public void setRpe(BigDecimal rpe) {
        this.rpe = rpe;
    }

    public String getSetType() {
        return setType;
    }

    public void setSetType(String setType) {
        this.setType = setType;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }
}