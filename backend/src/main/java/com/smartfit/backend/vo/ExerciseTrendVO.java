package com.smartfit.backend.vo;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ExerciseTrendVO {

    private Long exerciseId;

    private Integer sessionCount;

    private LocalDate startDate;
    private LocalDate endDate;

    private BigDecimal firstMaxWeightKg;
    private BigDecimal latestMaxWeightKg;

    private BigDecimal weightChangeKg;
    private BigDecimal weightChangePercent;

    private BigDecimal firstVolume;
    private BigDecimal latestVolume;

    private BigDecimal volumeChange;
    private BigDecimal volumeChangePercent;

    private BigDecimal averageRpeChange;

    private String trendStatus;


    public Long getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(Long exerciseId) {
        this.exerciseId = exerciseId;
    }

    public Integer getSessionCount() {
        return sessionCount;
    }

    public void setSessionCount(Integer sessionCount) {
        this.sessionCount = sessionCount;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getFirstMaxWeightKg() {
        return firstMaxWeightKg;
    }

    public void setFirstMaxWeightKg(BigDecimal firstMaxWeightKg) {
        this.firstMaxWeightKg = firstMaxWeightKg;
    }

    public BigDecimal getLatestMaxWeightKg() {
        return latestMaxWeightKg;
    }

    public void setLatestMaxWeightKg(BigDecimal latestMaxWeightKg) {
        this.latestMaxWeightKg = latestMaxWeightKg;
    }

    public BigDecimal getWeightChangeKg() {
        return weightChangeKg;
    }

    public void setWeightChangeKg(BigDecimal weightChangeKg) {
        this.weightChangeKg = weightChangeKg;
    }

    public BigDecimal getWeightChangePercent() {
        return weightChangePercent;
    }

    public void setWeightChangePercent(BigDecimal weightChangePercent) {
        this.weightChangePercent = weightChangePercent;
    }

    public BigDecimal getFirstVolume() {
        return firstVolume;
    }

    public void setFirstVolume(BigDecimal firstVolume) {
        this.firstVolume = firstVolume;
    }

    public BigDecimal getLatestVolume() {
        return latestVolume;
    }

    public void setLatestVolume(BigDecimal latestVolume) {
        this.latestVolume = latestVolume;
    }

    public BigDecimal getVolumeChange() {
        return volumeChange;
    }

    public void setVolumeChange(BigDecimal volumeChange) {
        this.volumeChange = volumeChange;
    }

    public BigDecimal getVolumeChangePercent() {
        return volumeChangePercent;
    }

    public void setVolumeChangePercent(BigDecimal volumeChangePercent) {
        this.volumeChangePercent = volumeChangePercent;
    }

    public BigDecimal getAverageRpeChange() {
        return averageRpeChange;
    }

    public void setAverageRpeChange(BigDecimal averageRpeChange) {
        this.averageRpeChange = averageRpeChange;
    }

    public String getTrendStatus() {
        return trendStatus;
    }

    public void setTrendStatus(String trendStatus) {
        this.trendStatus = trendStatus;
    }
}