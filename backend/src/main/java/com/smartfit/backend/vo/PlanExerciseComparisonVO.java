package com.smartfit.backend.vo;

import java.math.BigDecimal;

public class PlanExerciseComparisonVO {

    private Long exerciseId;
    private String exerciseName;

    private Integer targetSets;
    private Integer actualSets;
    private Integer successfulSets;

    private Integer targetRepsMin;
    private Integer targetRepsMax;

    private BigDecimal targetWeightKg;
    private BigDecimal targetRpe;

    private BigDecimal actualAverageRpe;

    private BigDecimal completionRate;

    private String status;


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

    public Integer getActualSets() {
        return actualSets;
    }

    public void setActualSets(Integer actualSets) {
        this.actualSets = actualSets;
    }

    public Integer getSuccessfulSets() {
        return successfulSets;
    }

    public void setSuccessfulSets(Integer successfulSets) {
        this.successfulSets = successfulSets;
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

    public BigDecimal getActualAverageRpe() {
        return actualAverageRpe;
    }

    public void setActualAverageRpe(BigDecimal actualAverageRpe) {
        this.actualAverageRpe = actualAverageRpe;
    }

    public BigDecimal getCompletionRate() {
        return completionRate;
    }

    public void setCompletionRate(BigDecimal completionRate) {
        this.completionRate = completionRate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}