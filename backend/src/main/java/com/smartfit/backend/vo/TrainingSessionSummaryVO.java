package com.smartfit.backend.vo;

import java.math.BigDecimal;

public class TrainingSessionSummaryVO {

    private Long sessionId;
    private Integer totalExercises;
    private Integer totalSets;
    private Integer totalReps;
    private BigDecimal totalVolume;
    private BigDecimal averageRpe;

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Integer getTotalExercises() {
        return totalExercises;
    }

    public void setTotalExercises(Integer totalExercises) {
        this.totalExercises = totalExercises;
    }

    public Integer getTotalSets() {
        return totalSets;
    }

    public void setTotalSets(Integer totalSets) {
        this.totalSets = totalSets;
    }

    public Integer getTotalReps() {
        return totalReps;
    }

    public void setTotalReps(Integer totalReps) {
        this.totalReps = totalReps;
    }

    public BigDecimal getTotalVolume() {
        return totalVolume;
    }

    public void setTotalVolume(BigDecimal totalVolume) {
        this.totalVolume = totalVolume;
    }

    public BigDecimal getAverageRpe() {
        return averageRpe;
    }

    public void setAverageRpe(BigDecimal averageRpe) {
        this.averageRpe = averageRpe;
    }
}