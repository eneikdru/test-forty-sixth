package com.eneik.generated.dto;

import java.time.LocalDateTime;

public class MaterialDetailDto {
    private Long id;
    private String title;
    private String pathogenType;
    private String content;
    private LocalDateTime createdAt;
    private String downloadUrl;

    public MaterialDetailDto() {
    }

    public MaterialDetailDto(Long id, String title, String pathogenType, String content, LocalDateTime createdAt, String downloadUrl) {
        this.id = id;
        this.title = title;
        this.pathogenType = pathogenType;
        this.content = content;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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
