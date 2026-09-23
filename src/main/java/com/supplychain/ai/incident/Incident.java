package com.supplychain.ai.incident;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.supplychain.ai.order.Order;
import com.supplychain.ai.warehouse.Warehouse;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "incident")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Incident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IncidentType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IncidentStatus status = IncidentStatus.OPEN;

    // Nullable: a WAREHOUSE_OVERLOAD incident may not tie to a single order.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_order_id")
    private Order relatedOrder;

    // Nullable: a SUPPLIER_DELAY incident may not tie to a specific warehouse.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_warehouse_id")
    private Warehouse relatedWarehouse;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime resolvedAt;

    public Incident() {
    }

    public Incident(Long id, IncidentType type, IncidentStatus status, Order relatedOrder,
                     Warehouse relatedWarehouse, String description, LocalDateTime createdAt,
                     LocalDateTime resolvedAt) {
        this.id = id;
        this.type = type;
        this.status = status;
        this.relatedOrder = relatedOrder;
        this.relatedWarehouse = relatedWarehouse;
        this.description = description;
        this.createdAt = createdAt;
        this.resolvedAt = resolvedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public IncidentType getType() {
        return type;
    }

    public void setType(IncidentType type) {
        this.type = type;
    }

    public IncidentStatus getStatus() {
        return status;
    }

    public void setStatus(IncidentStatus status) {
        this.status = status;
    }

    public Order getRelatedOrder() {
        return relatedOrder;
    }

    public void setRelatedOrder(Order relatedOrder) {
        this.relatedOrder = relatedOrder;
    }

    public Warehouse getRelatedWarehouse() {
        return relatedWarehouse;
    }

    public void setRelatedWarehouse(Warehouse relatedWarehouse) {
        this.relatedWarehouse = relatedWarehouse;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    // Small builder-ish helper kept as a plain static factory (no Lombok @Builder)
    // so IncidentService reads cleanly when assembling a new incident.
    public static Incident create(IncidentType type, Order relatedOrder, Warehouse relatedWarehouse, String description) {
        Incident incident = new Incident();
        incident.setType(type);
        incident.setStatus(IncidentStatus.OPEN);
        incident.setRelatedOrder(relatedOrder);
        incident.setRelatedWarehouse(relatedWarehouse);
        incident.setDescription(description);
        return incident;
    }
}
