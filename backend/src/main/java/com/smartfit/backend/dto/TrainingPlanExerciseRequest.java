package com.smartfit.backend.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class TrainingPlanExerciseRequest {

    @NotNull(message = "训练动作不能为空")
    private Long exerciseId;

    @NotNull(message = "目标组数不能为空")
    @Min(value = 1, message = "目标组数不能小于1")
    @Max(value = 20, message = "目标组数不能大于20")
    private Integer targetSets;

    @NotNull(message = "最低目标次数不能为空")
    @Min(value = 1, message = "最低目标次数不能小于1")
    @Max(value = 100, message = "最低目标次数不能大于100")
    private Integer targetRepsMin;

    @NotNull(message = "最高目标次数不能为空")
    @Min(value = 1, message = "最高目标次数不能小于1")
    @Max(value = 100, message = "最高目标次数不能大于100")
    private Integer targetRepsMax;

    @DecimalMin(value = "0.0", message = "目标重量不能小于0")
    private BigDecimal targetWeightKg;

    @DecimalMin(value = "1.0", message = "目标RPE不能小于1")
    @DecimalMax(value = "10.0", message = "目标RPE不能大于10")
    private BigDecimal targetRpe;

    private String notes;


    public Long getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(Long exerciseId) {
        this.exerciseId = exerciseId;
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