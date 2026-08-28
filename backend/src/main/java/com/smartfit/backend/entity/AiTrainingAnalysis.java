package com.smartfit.backend.entity;

import java.time.LocalDateTime;

public class AiTrainingAnalysis {

    private Long id;

    private Long userId;

    private Long sessionId;

    private String model;

    private Integer score;

    private String summary;

    private String positiveSignals;

    private String riskSignals;

    private String nextSessionAdvice;

    private String planAdjustments;

    private Boolean applied;

    private LocalDateTime appliedTime;

    private String promptVersion;

    private String rawResponse;

    private LocalDateTime createdTime;

    public Boolean getApplied() {
        return applied;
    }

    public void setApplied(Boolean applied) {
        this.applied = applied;
    }

    public LocalDateTime getAppliedTime() {
        return appliedTime;
    }

    public void setAppliedTime(LocalDateTime appliedTime) {
        this.appliedTime = appliedTime;
    }

    public String getPlanAdjustments() {
        return planAdjustments;
    }

    public void setPlanAdjustments(String planAdjustments) {
        this.planAdjustments = planAdjustments;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getPositiveSignals() {
        return positiveSignals;
    }

    public void setPositiveSignals(String positiveSignals) {
        this.positiveSignals = positiveSignals;
    }

    public String getRiskSignals() {
        return riskSignals;
    }

    public void setRiskSignals(String riskSignals) {
        this.riskSignals = riskSignals;
    }

    public String getNextSessionAdvice() {
        return nextSessionAdvice;
    }

    public void setNextSessionAdvice(String nextSessionAdvice) {
        this.nextSessionAdvice = nextSessionAdvice;
    }

    public String getPromptVersion() {
        return promptVersion;
    }

    public void setPromptVersion(String promptVersion) {
        this.promptVersion = promptVersion;
    }

    public String getRawResponse() {
        return rawResponse;
    }

    public void setRawResponse(String rawResponse) {
        this.rawResponse = rawResponse;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }
}