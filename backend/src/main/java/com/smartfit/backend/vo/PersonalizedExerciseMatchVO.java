package com.smartfit.backend.vo;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PersonalizedExerciseMatchVO {

    private Long exerciseId;

    private String exerciseName;

    private String muscleGroup;

    private String equipmentType;

    private String movementPattern;


    // 是否属于用户当前训练计划
    private Boolean inCurrentPlan;


    // 用户以前是否练过
    private Boolean hasHistory;


    // 最近一次训练数据
    private LocalDate lastSessionDate;

    private BigDecimal lastMaxWeightKg;

    private BigDecimal lastAverageRpe;


    // IMPROVING / STABLE / DECLINING / MIXED / INSUFFICIENT_DATA
    private String trendStatus;


    /*
     * 仅用于候选动作排序。
     *
     * 当前计划动作优先，其次是用户练过的动作。
     *
     * 这不是健身科学评分，
     * 只是产品推荐排序分。
     */
    private Integer priorityScore;


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

    public String getMuscleGroup() {
        return muscleGroup;
    }

    public void setMuscleGroup(String muscleGroup) {
        this.muscleGroup = muscleGroup;
    }

    public String getEquipmentType() {
        return equipmentType;
    }

    public void setEquipmentType(String equipmentType) {
        this.equipmentType = equipmentType;
    }

    public String getMovementPattern() {
        return movementPattern;
    }

    public void setMovementPattern(String movementPattern) {
        this.movementPattern = movementPattern;
    }

    public Boolean getInCurrentPlan() {
        return inCurrentPlan;
    }

    public void setInCurrentPlan(Boolean inCurrentPlan) {
        this.inCurrentPlan = inCurrentPlan;
    }

    public Boolean getHasHistory() {
        return hasHistory;
    }

    public void setHasHistory(Boolean hasHistory) {
        this.hasHistory = hasHistory;
    }

    public LocalDate getLastSessionDate() {
        return lastSessionDate;
    }

    public void setLastSessionDate(LocalDate lastSessionDate) {
        this.lastSessionDate = lastSessionDate;
    }

    public BigDecimal getLastMaxWeightKg() {
        return lastMaxWeightKg;
    }

    public void setLastMaxWeightKg(BigDecimal lastMaxWeightKg) {
        this.lastMaxWeightKg = lastMaxWeightKg;
    }

    public BigDecimal getLastAverageRpe() {
        return lastAverageRpe;
    }

    public void setLastAverageRpe(BigDecimal lastAverageRpe) {
        this.lastAverageRpe = lastAverageRpe;
    }

    public String getTrendStatus() {
        return trendStatus;
    }

    public void setTrendStatus(String trendStatus) {
        this.trendStatus = trendStatus;
    }

    public Integer getPriorityScore() {
        return priorityScore;
    }

    public void setPriorityScore(Integer priorityScore) {
        this.priorityScore = priorityScore;
    }
}