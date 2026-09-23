package com.supplychain.ai.incident;

import jakarta.validation.constraints.NotNull;

public class CreateIncidentRequest {

    @NotNull
    private IncidentType type;

    private Long relatedOrderId;

    private Long relatedWarehouseId;

    private String description;

    public IncidentType getType() {
        return type;
    }

    public void setType(IncidentType type) {
        this.type = type;
    }

    public Long getRelatedOrderId() {
        return relatedOrderId;
    }

    public void setRelatedOrderId(Long relatedOrderId) {
        this.relatedOrderId = relatedOrderId;
    }

    public Long getRelatedWarehouseId() {
        return relatedWarehouseId;
    }

    public void setRelatedWarehouseId(Long relatedWarehouseId) {
        this.relatedWarehouseId = relatedWarehouseId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
