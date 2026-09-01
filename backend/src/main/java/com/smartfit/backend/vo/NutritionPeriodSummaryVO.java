package com.smartfit.backend.vo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class NutritionPeriodSummaryVO {

    private LocalDate startDate;

    private LocalDate endDate;

    // 查询区间一共有多少天
    private Integer periodDays;

    // 实际有饮食记录的天数
    private Integer recordedDays;

    // 记录覆盖率
    private BigDecimal recordCoveragePercent;


    // 整个周期总摄入
    private BigDecimal totalCalories;

    private BigDecimal totalProteinG;

    private BigDecimal totalCarbohydrateG;

    private BigDecimal totalFatG;


    /*
     * 平均值只按照“有记录的天数”计算。
     */
    private BigDecimal averageCaloriesPerRecordedDay;

    private BigDecimal averageProteinGPerRecordedDay;

    private BigDecimal averageCarbohydrateGPerRecordedDay;

    private BigDecimal averageFatGPerRecordedDay;


    // 每天的具体数据，用于前端画趋势图
    private List<DailyNutritionSummaryVO> dailyData;


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

    public Integer getPeriodDays() {
        return periodDays;
    }

    public void setPeriodDays(Integer periodDays) {
        this.periodDays = periodDays;
    }

    public Integer getRecordedDays() {
        return recordedDays;
    }

    public void setRecordedDays(Integer recordedDays) {
        this.recordedDays = recordedDays;
    }

    public BigDecimal getRecordCoveragePercent() {
        return recordCoveragePercent;
    }

    public void setRecordCoveragePercent(
            BigDecimal recordCoveragePercent
    ) {
        this.recordCoveragePercent = recordCoveragePercent;
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

    public void setTotalCarbohydrateG(
            BigDecimal totalCarbohydrateG
    ) {
        this.totalCarbohydrateG = totalCarbohydrateG;
    }

    public BigDecimal getTotalFatG() {
        return totalFatG;
    }

    public void setTotalFatG(BigDecimal totalFatG) {
        this.totalFatG = totalFatG;
    }

    public BigDecimal getAverageCaloriesPerRecordedDay() {
        return averageCaloriesPerRecordedDay;
    }

    public void setAverageCaloriesPerRecordedDay(
            BigDecimal averageCaloriesPerRecordedDay
    ) {
        this.averageCaloriesPerRecordedDay =
                averageCaloriesPerRecordedDay;
    }

    public BigDecimal getAverageProteinGPerRecordedDay() {
        return averageProteinGPerRecordedDay;
    }

    public void setAverageProteinGPerRecordedDay(
            BigDecimal averageProteinGPerRecordedDay
    ) {
        this.averageProteinGPerRecordedDay =
                averageProteinGPerRecordedDay;
    }

    public BigDecimal getAverageCarbohydrateGPerRecordedDay() {
        return averageCarbohydrateGPerRecordedDay;
    }

    public void setAverageCarbohydrateGPerRecordedDay(
            BigDecimal averageCarbohydrateGPerRecordedDay
    ) {
        this.averageCarbohydrateGPerRecordedDay =
                averageCarbohydrateGPerRecordedDay;
    }

    public BigDecimal getAverageFatGPerRecordedDay() {
        return averageFatGPerRecordedDay;
    }

    public void setAverageFatGPerRecordedDay(
            BigDecimal averageFatGPerRecordedDay
    ) {
        this.averageFatGPerRecordedDay =
                averageFatGPerRecordedDay;
    }

    public List<DailyNutritionSummaryVO> getDailyData() {
        return dailyData;
    }

    public void setDailyData(
            List<DailyNutritionSummaryVO> dailyData
    ) {
        this.dailyData = dailyData;
    }
}