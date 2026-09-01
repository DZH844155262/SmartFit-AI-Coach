package com.smartfit.backend.dto.ai;

import com.smartfit.backend.vo.DailyNutritionSummaryVO;
import com.smartfit.backend.vo.ExerciseTrendVO;
import com.smartfit.backend.vo.NutritionPeriodSummaryVO;
import com.smartfit.backend.vo.TrainingPlanComparisonVO;
import com.smartfit.backend.vo.TrainingSessionSummaryVO;

import java.time.LocalDate;
import java.util.List;


public class AiCoachRequest {

    private Long userId;

    private Long sessionId;


    /*
     * 本次训练发生日期。
     *
     * Nutrition数据必须围绕这个日期查询，
     * 不能直接使用LocalDate.now()。
     */
    private LocalDate sessionDate;


    // 用户训练目标，例如 MUSCLE_GAIN
    private String goal;


    // 训练经验，例如 BEGINNER / INTERMEDIATE
    private String experienceLevel;


    // 本次训练的确定性统计结果
    private TrainingSessionSummaryVO sessionSummary;


    // 计划 vs 实际完成情况
    private TrainingPlanComparisonVO planComparison;


    // 本次涉及动作的历史趋势
    private List<ExerciseTrendVO> exerciseTrends;


    /*
     * 本次训练当天的营养摄入。
     *
     * 如果当天没有记录饮食，
     * 可以为null。
     */
    private DailyNutritionSummaryVO nutritionOnSessionDate;


    /*
     * 截止训练当天的最近7天营养统计。
     *
     * 例如：
     * sessionDate = 2026-09-01
     *
     * 查询：
     * 2026-08-26 ～ 2026-09-01
     */
    private NutritionPeriodSummaryVO recentNutrition7Days;



    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }


    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }


    public LocalDate getSessionDate() {
        return sessionDate;
    }

    public void setSessionDate(
            LocalDate sessionDate
    ) {
        this.sessionDate = sessionDate;
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

    public void setExperienceLevel(
            String experienceLevel
    ) {
        this.experienceLevel = experienceLevel;
    }


    public TrainingSessionSummaryVO getSessionSummary() {
        return sessionSummary;
    }

    public void setSessionSummary(
            TrainingSessionSummaryVO sessionSummary
    ) {
        this.sessionSummary = sessionSummary;
    }


    public TrainingPlanComparisonVO getPlanComparison() {
        return planComparison;
    }

    public void setPlanComparison(
            TrainingPlanComparisonVO planComparison
    ) {
        this.planComparison = planComparison;
    }


    public List<ExerciseTrendVO> getExerciseTrends() {
        return exerciseTrends;
    }

    public void setExerciseTrends(
            List<ExerciseTrendVO> exerciseTrends
    ) {
        this.exerciseTrends = exerciseTrends;
    }


    public DailyNutritionSummaryVO getNutritionOnSessionDate() {
        return nutritionOnSessionDate;
    }

    public void setNutritionOnSessionDate(
            DailyNutritionSummaryVO nutritionOnSessionDate
    ) {
        this.nutritionOnSessionDate =
                nutritionOnSessionDate;
    }


    public NutritionPeriodSummaryVO getRecentNutrition7Days() {
        return recentNutrition7Days;
    }

    public void setRecentNutrition7Days(
            NutritionPeriodSummaryVO recentNutrition7Days
    ) {
        this.recentNutrition7Days =
                recentNutrition7Days;
    }
}