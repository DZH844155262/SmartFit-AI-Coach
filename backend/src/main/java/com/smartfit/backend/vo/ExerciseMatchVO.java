package com.smartfit.backend.vo;

public class ExerciseMatchVO {

    private Long exerciseId;

    private String exerciseName;

    private String muscleGroup;

    private String equipmentType;

    private String movementPattern;


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

    public String getMuscleGroup() {
        return muscleGroup;
    }

    public void setMuscleGroup(String muscleGroup) {
        this.muscleGroup = muscleGroup;
    }

    public String getEquipmentType() {
        return equipmentType;
    }

    public void setEquipmentType(String equipmentType) {
        this.equipmentType = equipmentType;
    }

    public String getMovementPattern() {
        return movementPattern;
    }

    public void setMovementPattern(String movementPattern) {
        this.movementPattern = movementPattern;
    }
}