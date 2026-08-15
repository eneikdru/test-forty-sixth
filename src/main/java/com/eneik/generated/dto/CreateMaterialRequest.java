package com.eneik.generated.dto;

import java.util.Objects;

public class CreateMaterialRequest {
    private String title;
    private String pathogenType;
    private String content;
    private String metadata;

    public CreateMaterialRequest() {
    }

    public CreateMaterialRequest(String title, String pathogenType, String content, String metadata) {
        this.title = title;
        this.pathogenType = pathogenType;
        this.content = content;
        this.metadata = metadata;
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
}
