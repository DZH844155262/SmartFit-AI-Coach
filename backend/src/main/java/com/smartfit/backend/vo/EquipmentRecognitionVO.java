package com.smartfit.backend.vo;

import java.util.List;

public class EquipmentRecognitionVO {

    private String equipmentName;

    private String equipmentCategory;

    private List<String> possibleExercises;

    private List<String> targetMuscles;

    private String usage;

    private List<String> safetyNotes;

    private List<ExerciseMatchVO> matchedExercises;

    private List<PersonalizedExerciseMatchVO> personalizedMatches;
    /*
     * HIGH / MEDIUM / LOW
     *
     * 这是模型的自我判断，
     * 不是统计学意义上的真实概率。
     */
    private String recognitionConfidence;

    public List<PersonalizedExerciseMatchVO> getPersonalizedMatches() {
        return personalizedMatches;
    }

    public void setPersonalizedMatches(
            List<PersonalizedExerciseMatchVO> personalizedMatches
    ) {
        this.personalizedMatches = personalizedMatches;
    }
    public List<ExerciseMatchVO> getMatchedExercises() {
        return matchedExercises;
    }

    public void setMatchedExercises(
            List<ExerciseMatchVO> matchedExercises
    ) {
        this.matchedExercises = matchedExercises;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public void setEquipmentName(String equipmentName) {
        this.equipmentName = equipmentName;
    }

    public String getEquipmentCategory() {
        return equipmentCategory;
    }

    public void setEquipmentCategory(String equipmentCategory) {
        this.equipmentCategory = equipmentCategory;
    }

    public List<String> getPossibleExercises() {
        return possibleExercises;
    }

    public void setPossibleExercises(
            List<String> possibleExercises
    ) {
        this.possibleExercises = possibleExercises;
    }

    public List<String> getTargetMuscles() {
        return targetMuscles;
    }

    public void setTargetMuscles(
            List<String> targetMuscles
    ) {
        this.targetMuscles = targetMuscles;
    }

    public String getUsage() {
        return usage;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    public List<String> getSafetyNotes() {
        return safetyNotes;
    }

    public void setSafetyNotes(
            List<String> safetyNotes
    ) {
        this.safetyNotes = safetyNotes;
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