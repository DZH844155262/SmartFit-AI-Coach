package com.smartfit.backend.vo;


import java.util.List;


public class NutritionAnalysisVO {


    private String status;


    private String summary;


    private List<String> suggestions;



    public String getStatus() {
        return status;
    }


    public void setStatus(String status) {
        this.status = status;
    }


    public String getSummary() {
        return summary;
    }


    public void setSummary(String summary) {
        this.summary = summary;
    }


    public List<String> getSuggestions() {
        return suggestions;
    }


    public void setSuggestions(List<String> suggestions) {
        this.suggestions = suggestions;
    }
}