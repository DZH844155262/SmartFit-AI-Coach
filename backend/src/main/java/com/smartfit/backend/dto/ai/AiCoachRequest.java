package com.smartfit.backend.dto.ai;

import com.smartfit.backend.vo.ExerciseTrendVO;
import com.smartfit.backend.vo.TrainingPlanComparisonVO;
import com.smartfit.backend.vo.TrainingSessionSummaryVO;

import java.util.List;

public class AiCoachRequest {

    private Long userId;

    private Long sessionId;

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
}