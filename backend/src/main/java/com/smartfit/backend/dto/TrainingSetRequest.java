package com.smartfit.backend.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class TrainingSetRequest {

    @NotNull(message = "训练重量不能为空")
    @DecimalMin(value = "0.0", message = "训练重量不能小于0")
    private BigDecimal weightKg;

    @NotNull(message = "训练次数不能为空")
    @Min(value = 1, message = "训练次数至少为1次")
    @Max(value = 100, message = "单组训练次数不能超过100次")
    private Integer reps;

    @DecimalMin(value = "1.0", message = "RPE不能小于1")
    @DecimalMax(value = "10.0", message = "RPE不能大于10")
    private BigDecimal rpe;

    private String setType = "WORKING";


    public BigDecimal getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(BigDecimal weightKg) {
        this.weightKg = weightKg;
    }

    public Integer getReps() {
        return reps;
    }

    public void setReps(Integer reps) {
        this.reps = reps;
    }

    public BigDecimal getRpe() {
        return rpe;
    }

    public void setRpe(BigDecimal rpe) {
        this.rpe = rpe;
    }

    public String getSetType() {
        return setType;
    }

    public void setSetType(String setType) {
        this.setType = setType;
    }
}