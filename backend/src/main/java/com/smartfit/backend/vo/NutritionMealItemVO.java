package com.smartfit.backend.vo;

import java.math.BigDecimal;

public class NutritionMealItemVO {

    private Long foodId;
    private String foodName;
    private BigDecimal weightG;

    private BigDecimal calories;
    private BigDecimal proteinG;
    private BigDecimal carbohydrateG;
    private BigDecimal fatG;


    public Long getFoodId() {
        return foodId;
    }

    public void setFoodId(Long foodId) {
        this.foodId = foodId;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public BigDecimal getWeightG() {
        return weightG;
    }

    public void setWeightG(BigDecimal weightG) {
        this.weightG = weightG;
    }

    public BigDecimal getCalories() {
        return calories;
    }

    public void setCalories(BigDecimal calories) {
        this.calories = calories;
    }

    public BigDecimal getProteinG() {
        return proteinG;
    }

    public void setProteinG(BigDecimal proteinG) {
        this.proteinG = proteinG;
    }

    public BigDecimal getCarbohydrateG() {
        return carbohydrateG;
    }

    public void setCarbohydrateG(BigDecimal carbohydrateG) {
        this.carbohydrateG = carbohydrateG;
    }

    public BigDecimal getFatG() {
        return fatG;
    }

    public void setFatG(BigDecimal fatG) {
        this.fatG = fatG;
    }
}