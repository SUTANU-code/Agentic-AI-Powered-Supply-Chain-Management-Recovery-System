package com.supplychain.ai.shipment;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/shipments")
public class ShipmentController {

    private final ShipmentService shipmentService;

    public ShipmentController(ShipmentService shipmentService) {
        this.shipmentService = shipmentService;
    }

    @GetMapping
    public List<Shipment> getAll(@RequestParam(required = false) ShipmentStatus status) {
        return status == null ? shipmentService.findAll() : shipmentService.findByStatus(status);
    }

    @GetMapping("/{id}")
    public Shipment getById(@PathVariable Long id) {
        return shipmentService.findById(id);
    }

    @GetMapping("/order/{orderId}")
    public List<Shipment> getByOrder(@PathVariable Long orderId) {
        return shipmentService.findByOrder(orderId);
    }

    // Future Logistics Agent tool: "which shipments are overdue right now?"
    @GetMapping("/overdue")
    public List<Shipment> getOverdue() {
        return shipmentService.findOverdueInTransit();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Shipment create(@Valid @RequestBody CreateShipmentRequest request) {
        return shipmentService.create(request);
    }

    @PatchMapping("/{id}/dispatch")
    public Shipment dispatch(@PathVariable Long id) {
        return shipmentService.dispatch(id);
    }

    @PatchMapping("/{id}/deliver")
    public Shipment markDelivered(@PathVariable Long id) {
        return shipmentService.markDelivered(id);
    }

    @PatchMapping("/{id}/delay")
    public Shipment markDelayed(@PathVariable Long id) {
        return shipmentService.markDelayed(id);
    }
}
