package com.smartfit.backend.vo;

import java.util.List;

public class TrainingExerciseVO {

    private Long trainingExerciseId;
    private Long exerciseId;
    private String exerciseName;
    private Integer exerciseOrder;
    private String notes;

    private List<TrainingSetVO> sets;


    public Long getTrainingExerciseId() {
        return trainingExerciseId;
    }

    public void setTrainingExerciseId(Long trainingExerciseId) {
        this.trainingExerciseId = trainingExerciseId;
    }

    public Long getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(Long exerciseId) {
        this.exerciseId = exerciseId;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }

    public Integer getExerciseOrder() {
        return exerciseOrder;
    }

    public void setExerciseOrder(Integer exerciseOrder) {
        this.exerciseOrder = exerciseOrder;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<TrainingSetVO> getSets() {
        return sets;
    }

    public void setSets(List<TrainingSetVO> sets) {
        this.sets = sets;
    }
}