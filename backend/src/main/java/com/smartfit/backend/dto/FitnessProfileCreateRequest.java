package com.smartfit.backend.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class FitnessProfileCreateRequest {

    @NotBlank(message = "性别不能为空")
    private String sex;

    @NotNull(message = "年龄不能为空")
    @Min(value = 14, message = "年龄不能小于14岁")
    @Max(value = 100, message = "年龄不能大于100岁")
    private Integer age;

    @NotNull(message = "身高不能为空")
    @DecimalMin(value = "100.0", message = "身高不能小于100cm")
    @DecimalMax(value = "250.0", message = "身高不能大于250cm")
    private BigDecimal heightCm;

    @NotNull(message = "体重不能为空")
    @DecimalMin(value = "30.0", message = "体重不能小于30kg")
    @DecimalMax(value = "300.0", message = "体重不能大于300kg")
    private BigDecimal weightKg;

    @NotBlank(message = "训练目标不能为空")
    private String goal;

    @NotBlank(message = "训练经验不能为空")
    private String experienceLevel;

    @NotNull(message = "每周训练频率不能为空")
    @Min(value = 1, message = "每周至少训练1次")
    @Max(value = 7, message = "每周训练频率不能超过7次")
    private Integer weeklyFrequency;


    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public BigDecimal getHeightCm() {
        return heightCm;
    }

    public void setHeightCm(BigDecimal heightCm) {
        this.heightCm = heightCm;
    }

    public BigDecimal getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(BigDecimal weightKg) {
        this.weightKg = weightKg;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public String getExperienceLevel() {
        return experienceLevel;
    }

    public void setExperienceLevel(String experienceLevel) {
        this.experienceLevel = experienceLevel;
    }

    public Integer getWeeklyFrequency() {
        return weeklyFrequency;
    }

    public void setWeeklyFrequency(Integer weeklyFrequency) {
        this.weeklyFrequency = weeklyFrequency;
    }
}