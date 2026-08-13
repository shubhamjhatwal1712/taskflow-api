package com.shubham.taskflow.dto;

import java.time.LocalDateTime;

public class ProjectResponse {

    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private int taskCount;

    public ProjectResponse(Long id, String name, String description, LocalDateTime createdAt, int taskCount) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
        this.taskCount = taskCount;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public int getTaskCount() {
        return taskCount;
    }
}
