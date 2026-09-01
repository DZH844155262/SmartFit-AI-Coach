package com.smartfit.backend.dto.ai;

import com.smartfit.backend.vo.PlanAdjustmentProposalVO;
import com.smartfit.backend.vo.NutritionAnalysisVO;

import java.util.List;

public class AiCoachResponse {

    private Integer score;

    private String summary;

    private List<String> positiveSignals;

    private List<String> riskSignals;

    private List<String> nextSessionAdvice;

    // 新增：下一次训练计划的结构化调整建议
    private List<PlanAdjustmentProposalVO> planAdjustments;

    // 新增：营养分析建议
    private NutritionAnalysisVO nutritionAnalysis;


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

    public List<PlanAdjustmentProposalVO> getPlanAdjustments() {
        return planAdjustments;
    }

    public void setPlanAdjustments(
            List<PlanAdjustmentProposalVO> planAdjustments
    ) {
        this.planAdjustments = planAdjustments;
    }
    public NutritionAnalysisVO getNutritionAnalysis() {
        return nutritionAnalysis;
    }


    public void setNutritionAnalysis(
            NutritionAnalysisVO nutritionAnalysis
    ) {
        this.nutritionAnalysis = nutritionAnalysis;
    }
}