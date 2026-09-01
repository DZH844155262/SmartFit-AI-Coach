package com.smartfit.backend.vo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class NutritionMealCreateVO {

    private Long mealId;

    private LocalDate mealDate;

    private String mealType;

    private BigDecimal totalCalories;

    private BigDecimal totalProteinG;

    private BigDecimal totalCarbohydrateG;

    private BigDecimal totalFatG;

    private List<NutritionMealItemVO> items;


    public Long getMealId() {
        return mealId;
    }

    public void setMealId(Long mealId) {
        this.mealId = mealId;
    }

    public LocalDate getMealDate() {
        return mealDate;
    }

    public void setMealDate(LocalDate mealDate) {
        this.mealDate = mealDate;
    }

    public String getMealType() {
        return mealType;
    }

    public void setMealType(String mealType) {
        this.mealType = mealType;
    }

    public BigDecimal getTotalCalories() {
        return totalCalories;
    }

    public void setTotalCalories(BigDecimal totalCalories) {
        this.totalCalories = totalCalories;
    }

    public BigDecimal getTotalProteinG() {
        return totalProteinG;
    }

    public void setTotalProteinG(BigDecimal totalProteinG) {
        this.totalProteinG = totalProteinG;
    }

    public BigDecimal getTotalCarbohydrateG() {
        return totalCarbohydrateG;
    }

    public void setTotalCarbohydrateG(BigDecimal totalCarbohydrateG) {
        this.totalCarbohydrateG = totalCarbohydrateG;
    }

    public BigDecimal getTotalFatG() {
        return totalFatG;
    }

    public void setTotalFatG(BigDecimal totalFatG) {
        this.totalFatG = totalFatG;
    }

    public List<NutritionMealItemVO> getItems() {
        return items;
    }

    public void setItems(List<NutritionMealItemVO> items) {
        this.items = items;
    }
}