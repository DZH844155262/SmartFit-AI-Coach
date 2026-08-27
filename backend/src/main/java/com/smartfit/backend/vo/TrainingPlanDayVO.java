package com.smartfit.backend.vo;

import java.util.List;

public class TrainingPlanDayVO {

    private Long id;

    private Integer dayNumber;

    private String title;

    private String notes;

    private List<TrainingPlanExerciseVO> exercises;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getDayNumber() {
        return dayNumber;
    }

    public void setDayNumber(Integer dayNumber) {
        this.dayNumber = dayNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<TrainingPlanExerciseVO> getExercises() {
        return exercises;
    }

    public void setExercises(List<TrainingPlanExerciseVO> exercises) {
        this.exercises = exercises;
    }
}