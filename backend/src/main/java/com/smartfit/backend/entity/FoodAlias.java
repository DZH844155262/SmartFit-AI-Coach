package com.smartfit.backend.entity;


import java.time.LocalDateTime;


public class FoodAlias {


    private Long id;


    private Long foodId;


    private String alias;


    private LocalDateTime createdTime;



    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public Long getFoodId() {
        return foodId;
    }


    public void setFoodId(Long foodId) {
        this.foodId = foodId;
    }


    public String getAlias() {
        return alias;
    }


    public void setAlias(String alias) {
        this.alias = alias;
    }


    public LocalDateTime getCreatedTime() {
        return createdTime;
    }


    public void setCreatedTime(
            LocalDateTime createdTime
    ) {
        this.createdTime = createdTime;
    }
}