package com.smartfit.backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class NutritionVisionMealItemRequest {

    @NotNull(message = "foodId不能为空")
    private Long foodId;


    /*
     * Vision给出的原始估计重量。
     */
    @NotNull(message = "estimatedWeightG不能为空")
    @DecimalMin(
            value = "0.1",
            message = "estimatedWeightG必须大于0"
    )
    private BigDecimal estimatedWeightG;


    /*
     * 用户修改后的重量。
     *
     * 用户没有修改时允许为null。
     */
    @DecimalMin(
            value = "0.1",
            message = "confirmedWeightG必须大于0"
    )
    private BigDecimal confirmedWeightG;


    public Long getFoodId() {
        return foodId;
    }

    public void setFoodId(Long foodId) {
        this.foodId = foodId;
    }

    public BigDecimal getEstimatedWeightG() {
        return estimatedWeightG;
    }

    public void setEstimatedWeightG(
            BigDecimal estimatedWeightG
    ) {
        this.estimatedWeightG = estimatedWeightG;
    }

    public BigDecimal getConfirmedWeightG() {
        return confirmedWeightG;
    }

    public void setConfirmedWeightG(
            BigDecimal confirmedWeightG
    ) {
        this.confirmedWeightG = confirmedWeightG;
    }
}