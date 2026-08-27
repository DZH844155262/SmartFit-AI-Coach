package com.smartfit.backend.vo;

import java.math.BigDecimal;

public class TrainingPlanExerciseVO {

    private Long planExerciseId;

    private Long exerciseId;

    private String exerciseName;

    // 来自 exercise 表：通用动作说明
    private String instructions;

    private Integer exerciseOrder;

    private Integer targetSets;

    private Integer targetRepsMin;

    private Integer targetRepsMax;

    private BigDecimal targetWeightKg;

    private BigDecimal targetRpe;

    // 来自 training_plan_exercise：
    // 当前计划针对这个动作的个性化提示
    private String notes;


    public Long getPlanExerciseId() {
        return planExerciseId;
    }

    public void setPlanExerciseId(Long planExerciseId) {
        this.planExerciseId = planExerciseId;
    }

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

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public Integer getExerciseOrder() {
        return exerciseOrder;
    }

    public void setExerciseOrder(Integer exerciseOrder) {
        this.exerciseOrder = exerciseOrder;
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}