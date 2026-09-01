package com.smartfit.backend.vo;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DailyNutritionSummaryVO {

    private LocalDate date;

    private BigDecimal totalCalories;

    private BigDecimal totalProteinG;

    private BigDecimal totalCarbohydrateG;

    private BigDecimal totalFatG;


    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
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
}