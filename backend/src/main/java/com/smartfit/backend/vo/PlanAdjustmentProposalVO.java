package com.smartfit.backend.vo;

import java.math.BigDecimal;

public class PlanAdjustmentProposalVO {

    // 哪个动作
    private Long exerciseId;

    private String exerciseName;


    /*
     * KEEP       保持
     * INCREASE   增加负荷
     * DECREASE   降低负荷
     * ADJUST     调整组数/次数等
     *
     * 第一版先用String，
     * 暂时不用enum，避免大模型偶尔返回其他值导致JSON解析直接失败。
     */
    private String action;


    // 当前计划重量
    private BigDecimal currentWeight;

    // AI建议的下一次重量
    private BigDecimal recommendedWeight;


    // AI建议下一次目标
    private Integer targetSets;

    private Integer targetRepsMin;

    private Integer targetRepsMax;

    private BigDecimal targetRpe;


    // 为什么这样调整
    private String reason;


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

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public BigDecimal getCurrentWeight() {
        return currentWeight;
    }

    public void setCurrentWeight(BigDecimal currentWeight) {
        this.currentWeight = currentWeight;
    }

    public BigDecimal getRecommendedWeight() {
        return recommendedWeight;
    }

    public void setRecommendedWeight(BigDecimal recommendedWeight) {
        this.recommendedWeight = recommendedWeight;
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

    public BigDecimal getTargetRpe() {
        return targetRpe;
    }

    public void setTargetRpe(BigDecimal targetRpe) {
        this.targetRpe = targetRpe;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}