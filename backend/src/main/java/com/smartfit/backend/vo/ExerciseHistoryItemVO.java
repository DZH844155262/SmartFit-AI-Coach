package com.smartfit.backend.vo;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ExerciseHistoryItemVO {

    private Long sessionId;
    private LocalDate sessionDate;

    private BigDecimal maxWeightKg;

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

    public LocalDate getSessionDate() {
        return sessionDate;
    }

    public void setSessionDate(LocalDate sessionDate) {
        this.sessionDate = sessionDate;
    }

    public BigDecimal getMaxWeightKg() {
        return maxWeightKg;
    }

    public void setMaxWeightKg(BigDecimal maxWeightKg) {
        this.maxWeightKg = maxWeightKg;
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