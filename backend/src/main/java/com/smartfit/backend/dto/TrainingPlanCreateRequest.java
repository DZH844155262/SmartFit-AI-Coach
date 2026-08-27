package com.smartfit.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public class TrainingPlanCreateRequest {

    @NotBlank(message = "训练计划名称不能为空")
    private String name;

    @NotBlank(message = "训练目标不能为空")
    private String goal;

    @NotNull(message = "计划开始日期不能为空")
    private LocalDate startDate;

    private LocalDate endDate;

    @NotNull(message = "每周训练频率不能为空")
    @Min(value = 1, message = "每周训练频率不能小于1")
    @Max(value = 7, message = "每周训练频率不能大于7")
    private Integer weeklyFrequency;

    @Valid
    @NotEmpty(message = "训练计划必须至少包含一个训练日")
    private List<TrainingPlanDayRequest> days;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Integer getWeeklyFrequency() {
        return weeklyFrequency;
    }

    public void setWeeklyFrequency(Integer weeklyFrequency) {
        this.weeklyFrequency = weeklyFrequency;
    }

    public List<TrainingPlanDayRequest> getDays() {
        return days;
    }

    public void setDays(List<TrainingPlanDayRequest> days) {
        this.days = days;
    }
}