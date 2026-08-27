package com.smartfit.backend.vo;

import java.math.BigDecimal;
import java.util.List;

public class TrainingPlanComparisonVO {

    private Long sessionId;
    private Long planDayId;

    private String planDayTitle;

    private Integer totalTargetSets;
    private Integer totalSuccessfulSets;

    private BigDecimal overallCompletionRate;

    private List<PlanExerciseComparisonVO> exercises;


    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Long getPlanDayId() {
        return planDayId;
    }

    public void setPlanDayId(Long planDayId) {
        this.planDayId = planDayId;
    }

    public String getPlanDayTitle() {
        return planDayTitle;
    }

    public void setPlanDayTitle(String planDayTitle) {
        this.planDayTitle = planDayTitle;
    }

    public Integer getTotalTargetSets() {
        return totalTargetSets;
    }

    public void setTotalTargetSets(Integer totalTargetSets) {
        this.totalTargetSets = totalTargetSets;
    }

    public Integer getTotalSuccessfulSets() {
        return totalSuccessfulSets;
    }

    public void setTotalSuccessfulSets(Integer totalSuccessfulSets) {
        this.totalSuccessfulSets = totalSuccessfulSets;
    }

    public BigDecimal getOverallCompletionRate() {
        return overallCompletionRate;
    }

    public void setOverallCompletionRate(BigDecimal overallCompletionRate) {
        this.overallCompletionRate = overallCompletionRate;
    }

    public List<PlanExerciseComparisonVO> getExercises() {
        return exercises;
    }

    public void setExercises(List<PlanExerciseComparisonVO> exercises) {
        this.exercises = exercises;
    }
}