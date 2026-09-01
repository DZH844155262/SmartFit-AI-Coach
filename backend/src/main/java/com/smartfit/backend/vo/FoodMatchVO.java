package com.smartfit.backend.vo;

import java.math.BigDecimal;


public class FoodMatchVO {


    /**
     * 数据库中的food id
     */
    private Long foodId;


    /**
     * Vision识别名称
     *
     * 例如：
     * 生鸡胸肉
     */
    private String recognizedName;



    /**
     * 数据库标准名称
     *
     * 例如：
     * 鸡胸肉
     */
    private String matchedFoodName;



    /**
     * 匹配程度
     *
     * HIGH
     * MEDIUM
     * LOW
     */
    private String matchConfidence;



    /**
     * AI建议重量
     */
    private BigDecimal suggestedWeightG;



    public Long getFoodId() {
        return foodId;
    }


    public void setFoodId(Long foodId) {
        this.foodId = foodId;
    }


    public String getRecognizedName() {
        return recognizedName;
    }


    public void setRecognizedName(String recognizedName) {
        this.recognizedName = recognizedName;
    }


    public String getMatchedFoodName() {
        return matchedFoodName;
    }


    public void setMatchedFoodName(String matchedFoodName) {
        this.matchedFoodName = matchedFoodName;
    }


    public String getMatchConfidence() {
        return matchConfidence;
    }


    public void setMatchConfidence(String matchConfidence) {
        this.matchConfidence = matchConfidence;
    }


    public BigDecimal getSuggestedWeightG() {
        return suggestedWeightG;
    }


    public void setSuggestedWeightG(BigDecimal suggestedWeightG) {
        this.suggestedWeightG = suggestedWeightG;
    }
}