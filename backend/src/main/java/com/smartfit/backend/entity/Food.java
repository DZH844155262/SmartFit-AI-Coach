package com.smartfit.backend.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Food {

    private Long id;

    private String name;

    private String nameEn;

    private BigDecimal caloriesPer100g;

    private BigDecimal proteinPer100g;

    private BigDecimal carbohydratePer100g;

    private BigDecimal fatPer100g;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public BigDecimal getCaloriesPer100g() {
        return caloriesPer100g;
    }

    public void setCaloriesPer100g(BigDecimal caloriesPer100g) {
        this.caloriesPer100g = caloriesPer100g;
    }

    public BigDecimal getProteinPer100g() {
        return proteinPer100g;
    }

    public void setProteinPer100g(BigDecimal proteinPer100g) {
        this.proteinPer100g = proteinPer100g;
    }

    public BigDecimal getCarbohydratePer100g() {
        return carbohydratePer100g;
    }

    public void setCarbohydratePer100g(
            BigDecimal carbohydratePer100g
    ) {
        this.carbohydratePer100g =
                carbohydratePer100g;
    }

    public BigDecimal getFatPer100g() {
        return fatPer100g;
    }

    public void setFatPer100g(BigDecimal fatPer100g) {
        this.fatPer100g = fatPer100g;
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