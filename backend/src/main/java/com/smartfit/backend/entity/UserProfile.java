package com.smartfit.backend.entity;


import java.math.BigDecimal;
import java.time.LocalDateTime;


public class UserProfile {


    private Long id;


    private String nickname;


    private Integer age;


    private BigDecimal height;


    private BigDecimal weight;


    private LocalDateTime createdTime;


    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


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


    public LocalDateTime getCreatedTime() {
        return createdTime;
    }


    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }
}