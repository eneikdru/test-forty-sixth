package com.eneik.generated.dto;

import java.time.Instant;

public class MaterialResponse {
    private String id;
    private String title;
    private String pathogenType;
    private String content;
    private String metadata;
    private String status;
    private Instant createdAt;

    public MaterialResponse() {
    }

    public MaterialResponse(String id, String title, String pathogenType, String content, String metadata, String status, Instant createdAt) {
        this.id = id;
        this.title = title;
        this.pathogenType = pathogenType;
        this.content = content;
        this.metadata = metadata;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
