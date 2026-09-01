package com.smartfit.backend.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class TrainingSetAppendRequest {

    /*
     * 允许0kg：
     * 以后自重动作也可能使用。
     */
    @NotNull(message = "weightKg不能为空")
    @DecimalMin(
            value = "0.0",
            message = "weightKg不能小于0"
    )
    private BigDecimal weightKg;


    @NotNull(message = "reps不能为空")
    @Min(
            value = 1,
            message = "reps必须至少为1"
    )
    private Integer reps;


    /*
     * RPE允许不填写。
     *
     * 如果填写，则要求1~10。
     */
    @DecimalMin(
            value = "1.0",
            message = "RPE不能小于1"
    )
    @DecimalMax(
            value = "10.0",
            message = "RPE不能大于10"
    )
    private BigDecimal rpe;


    /*
     * WORKING / WARMUP 等。
     *
     * 如果不传，
     * Service默认使用WORKING。
     */
    private String setType;


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