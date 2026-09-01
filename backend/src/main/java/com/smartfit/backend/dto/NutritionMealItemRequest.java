package com.smartfit.backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class NutritionMealItemRequest {

    @NotNull(message = "foodId不能为空")
    private Long foodId;

    @NotNull(message = "weightG不能为空")
    @DecimalMin(
            value = "0.1",
            message = "食物重量必须大于0"
    )
    private BigDecimal weightG;


    public Long getFoodId() {
        return foodId;
    }

    public void setFoodId(Long foodId) {
        this.foodId = foodId;
    }

    public BigDecimal getWeightG() {
        return weightG;
    }

    public void setWeightG(BigDecimal weightG) {
        this.weightG = weightG;
    }
}