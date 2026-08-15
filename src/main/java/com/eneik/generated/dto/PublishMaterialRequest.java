package com.eneik.generated.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class PublishMaterialRequest {

    @NotBlank(message = "Status is required")
    @Pattern(regexp = "PUBLISHED", message = "Status must be PUBLISHED")
    private String status;

    private String comment;

    public PublishMaterialRequest() {
    }

    public PublishMaterialRequest(String status, String comment) {
        this.status = status;
        this.comment = comment;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
