package com.supplychain.ai.shipment;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class CreateShipmentRequest {

    @NotNull
    private Long orderId;

    @NotNull
    private Long warehouseId;

    private String courier;

    private LocalDateTime expectedDelivery;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getCourier() {
        return courier;
    }

    public void setCourier(String courier) {
        this.courier = courier;
    }

    public LocalDateTime getExpectedDelivery() {
        return expectedDelivery;
    }

    public void setExpectedDelivery(LocalDateTime expectedDelivery) {
        this.expectedDelivery = expectedDelivery;
    }
}
