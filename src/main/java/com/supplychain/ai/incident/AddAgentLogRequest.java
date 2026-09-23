package com.supplychain.ai.incident;

import jakarta.validation.constraints.NotBlank;

public class AddAgentLogRequest {

    @NotBlank
    private String agentName;

    @NotBlank
    private String action;

    private String message;

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
