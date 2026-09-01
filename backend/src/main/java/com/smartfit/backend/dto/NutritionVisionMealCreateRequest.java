package com.smartfit.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public class NutritionVisionMealCreateRequest {

    @NotNull(message = "mealDate不能为空")
    private LocalDate mealDate;

    @NotNull(message = "mealType不能为空")
    private String mealType;

    private String notes;


    @Valid
    @NotEmpty(message = "至少需要确认一种食物")
    private List<NutritionVisionMealItemRequest> items;


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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<NutritionVisionMealItemRequest> getItems() {
        return items;
    }

    public void setItems(
            List<NutritionVisionMealItemRequest> items
    ) {
        this.items = items;
    }
}