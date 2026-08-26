package com.smartfit.backend.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TrainingSession {

    private Long id;
    private Long userId;
    private LocalDate sessionDate;
    private String title;
    private String notes;
    private LocalDateTime startedTime;
    private LocalDateTime endedTime;
    private LocalDateTime createdTime;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDate getSessionDate() {
        return sessionDate;
    }

    public void setSessionDate(LocalDate sessionDate) {
        this.sessionDate = sessionDate;
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

    public LocalDateTime getStartedTime() {
        return startedTime;
    }

    public void setStartedTime(LocalDateTime startedTime) {
        this.startedTime = startedTime;
    }

    public LocalDateTime getEndedTime() {
        return endedTime;
    }

    public void setEndedTime(LocalDateTime endedTime) {
        this.endedTime = endedTime;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }
}