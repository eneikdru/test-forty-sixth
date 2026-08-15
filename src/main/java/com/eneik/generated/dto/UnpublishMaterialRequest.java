package com.eneik.generated.dto;

public class UnpublishMaterialRequest {
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
