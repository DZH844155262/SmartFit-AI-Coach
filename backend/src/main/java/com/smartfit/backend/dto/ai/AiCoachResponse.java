package com.smartfit.backend.dto.ai;

import java.util.List;

public class AiCoachResponse {

    // 0～100，本次训练综合评分
    private Integer score;

    // 一段非常简短的总体评价
    private String summary;

    // 积极信号
    private List<String> positiveSignals;

    // 需要注意的信号
    private List<String> riskSignals;

    // 下一次训练建议
    private List<String> nextSessionAdvice;


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

    public List<String> getPositiveSignals() {
        return positiveSignals;
    }

    public void setPositiveSignals(
            List<String> positiveSignals
    ) {
        this.positiveSignals = positiveSignals;
    }

    public List<String> getRiskSignals() {
        return riskSignals;
    }

    public void setRiskSignals(
            List<String> riskSignals
    ) {
        this.riskSignals = riskSignals;
    }

    public List<String> getNextSessionAdvice() {
        return nextSessionAdvice;
    }

    public void setNextSessionAdvice(
            List<String> nextSessionAdvice
    ) {
        this.nextSessionAdvice = nextSessionAdvice;
    }
}