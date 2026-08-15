package com.eneik.generated.dto;

import java.time.LocalDateTime;

public class MaterialSummaryDto {
    private Long id;
    private String title;
    private String pathogenType;
    private LocalDateTime createdAt;
    private String downloadUrl;

    public MaterialSummaryDto() {
    }

    public MaterialSummaryDto(Long id, String title, String pathogenType, LocalDateTime createdAt, String downloadUrl) {
        this.id = id;
        this.title = title;
        this.pathogenType = pathogenType;
        this.createdAt = createdAt;
        this.downloadUrl = downloadUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPathogenType() {
        return pathogenType;
    }

    public void setPathogenType(String pathogenType) {
        this.pathogenType = pathogenType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
}
