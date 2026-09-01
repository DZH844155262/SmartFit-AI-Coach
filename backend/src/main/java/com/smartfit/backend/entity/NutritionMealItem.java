package com.smartfit.backend.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class NutritionMealItem {

    private Long id;

    private Long mealId;

    private Long foodId;

    private String foodName;

    private BigDecimal portionValue;

    private String portionUnit;

    private BigDecimal estimatedWeightG;

    private BigDecimal confirmedWeightG;

    private BigDecimal usedWeightG;

    private String weightSource;

    private String recordSource;

    private BigDecimal calories;

    private BigDecimal proteinG;

    private BigDecimal carbohydrateG;

    private BigDecimal fatG;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMealId() {
        return mealId;
    }

    public void setMealId(Long mealId) {
        this.mealId = mealId;
    }

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

    public BigDecimal getPortionValue() {
        return portionValue;
    }

    public void setPortionValue(BigDecimal portionValue) {
        this.portionValue = portionValue;
    }

    public String getPortionUnit() {
        return portionUnit;
    }

    public void setPortionUnit(String portionUnit) {
        this.portionUnit = portionUnit;
    }

    public BigDecimal getEstimatedWeightG() {
        return estimatedWeightG;
    }

    public void setEstimatedWeightG(BigDecimal estimatedWeightG) {
        this.estimatedWeightG = estimatedWeightG;
    }

    public BigDecimal getConfirmedWeightG() {
        return confirmedWeightG;
    }

    public void setConfirmedWeightG(BigDecimal confirmedWeightG) {
        this.confirmedWeightG = confirmedWeightG;
    }

    public BigDecimal getUsedWeightG() {
        return usedWeightG;
    }

    public void setUsedWeightG(BigDecimal usedWeightG) {
        this.usedWeightG = usedWeightG;
    }

    public String getWeightSource() {
        return weightSource;
    }

    public void setWeightSource(String weightSource) {
        this.weightSource = weightSource;
    }

    public String getRecordSource() {
        return recordSource;
    }

    public void setRecordSource(String recordSource) {
        this.recordSource = recordSource;
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

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }
}