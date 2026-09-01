package com.smartfit.backend.vo;

import java.math.BigDecimal;

public class FoodVisionItemVO {

    /*
     * AI识别出的食物名称
     *
     * 例如：
     * 鸡胸肉
     * 熟米饭
     * 西兰花
     */
    private String foodName;


    /*
     * 给用户看的自然语言份量描述
     *
     * 例如：
     * 一掌大小
     * 一小碗
     * 两个
     */
    private String portionDescription;


    /*
     * AI估算重量范围。
     *
     * 注意：
     * 只是视觉估算，不是真实称重。
     */
    private BigDecimal estimatedWeightMinG;

    private BigDecimal estimatedWeightMaxG;


    /*
     * 给前端默认填入输入框的建议重量。
     *
     * 用户可以直接接受，
     * 也可以修改成真实重量。
     */
    private BigDecimal suggestedWeightG;


    /*
     * HIGH / MEDIUM / LOW
     *
     * 只是模型对“识别结果”的自我判断，
     * 不是统计学置信概率。
     */
    private String recognitionConfidence;


    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getPortionDescription() {
        return portionDescription;
    }

    public void setPortionDescription(
            String portionDescription
    ) {
        this.portionDescription = portionDescription;
    }

    public BigDecimal getEstimatedWeightMinG() {
        return estimatedWeightMinG;
    }

    public void setEstimatedWeightMinG(
            BigDecimal estimatedWeightMinG
    ) {
        this.estimatedWeightMinG = estimatedWeightMinG;
    }

    public BigDecimal getEstimatedWeightMaxG() {
        return estimatedWeightMaxG;
    }

    public void setEstimatedWeightMaxG(
            BigDecimal estimatedWeightMaxG
    ) {
        this.estimatedWeightMaxG = estimatedWeightMaxG;
    }

    public BigDecimal getSuggestedWeightG() {
        return suggestedWeightG;
    }

    public void setSuggestedWeightG(
            BigDecimal suggestedWeightG
    ) {
        this.suggestedWeightG = suggestedWeightG;
    }

    public String getRecognitionConfidence() {
        return recognitionConfidence;
    }

    public void setRecognitionConfidence(
            String recognitionConfidence
    ) {
        this.recognitionConfidence =
                recognitionConfidence;
    }
}