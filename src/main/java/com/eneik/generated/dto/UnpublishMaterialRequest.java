package com.eneik.generated.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class UnpublishMaterialRequest {

    @NotBlank(message = "Confirmation state is required")
    @Pattern(regexp = "CONFIRMED", message = "Confirmation state MUST be set to CONFIRMED to unpublish material")
    private String confirmationState;

    private String reason;

    public UnpublishMaterialRequest() {
    }

    public UnpublishMaterialRequest(String confirmationState, String reason) {
        this.confirmationState = confirmationState;
        this.reason = reason;
    }

    public String getConfirmationState() {
        return confirmationState;
    }

    public void setConfirmationState(String confirmationState) {
        this.confirmationState = confirmationState;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
