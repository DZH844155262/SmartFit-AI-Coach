package com.smartfit.backend.vo;

import java.math.BigDecimal;

public class TrainingSessionStartVO {

    private Long sessionId;

    private Long trainingExerciseId;

    private Long planDayId;

    private Long exerciseId;

    private String exerciseName;

    private Integer targetSets;

    private Integer targetRepsMin;

    private Integer targetRepsMax;

    private BigDecimal targetWeightKg;

    private BigDecimal targetRpe;

    private String notes;


    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Long getTrainingExerciseId() {
        return trainingExerciseId;
    }

    public void setTrainingExerciseId(Long trainingExerciseId) {
        this.trainingExerciseId = trainingExerciseId;
    }

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

    public String getExerciseName() {
        return exerciseName;
    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }

    public Integer getTargetSets() {
        return targetSets;
    }

    public void setTargetSets(Integer targetSets) {
        this.targetSets = targetSets;
    }

    public Integer getTargetRepsMin() {
        return targetRepsMin;
    }

    public void setTargetRepsMin(Integer targetRepsMin) {
        this.targetRepsMin = targetRepsMin;
    }

    public Integer getTargetRepsMax() {
        return targetRepsMax;
    }

    public void setTargetRepsMax(Integer targetRepsMax) {
        this.targetRepsMax = targetRepsMax;
    }

    public BigDecimal getTargetWeightKg() {
        return targetWeightKg;
    }

    public void setTargetWeightKg(BigDecimal targetWeightKg) {
        this.targetWeightKg = targetWeightKg;
    }

    public BigDecimal getTargetRpe() {
        return targetRpe;
    }

    public void setTargetRpe(BigDecimal targetRpe) {
        this.targetRpe = targetRpe;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}