package com.smartfit.backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class UserProfileRequest {

    @NotBlank(message = "昵称不能为空")
    private String nickname;

    @NotNull(message = "年龄不能为空")
    @Min(value = 14, message = "年龄不能小于14岁")
    @Max(value = 100, message = "年龄不能大于100岁")
    private Integer age;

    @NotNull(message = "身高不能为空")
    private BigDecimal height;

    @NotNull(message = "体重不能为空")
    private BigDecimal weight;


    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public BigDecimal getHeight() {
        return height;
    }

    public void setHeight(BigDecimal height) {
        this.height = height;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }
}