package com.supplychain.ai.shipment;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.supplychain.ai.common.ResourceNotFoundException;
import com.supplychain.ai.order.Order;
import com.supplychain.ai.order.OrderRepository;
import com.supplychain.ai.warehouse.Warehouse;
import com.supplychain.ai.warehouse.WarehouseRepository;

@Service
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final OrderRepository orderRepository;
    private final WarehouseRepository warehouseRepository;

    public ShipmentService(ShipmentRepository shipmentRepository,
                            OrderRepository orderRepository,
                            WarehouseRepository warehouseRepository) {
        this.shipmentRepository = shipmentRepository;
        this.orderRepository = orderRepository;
        this.warehouseRepository = warehouseRepository;
    }

    public List<Shipment> findAll() {
        return shipmentRepository.findAll();
    }

    public Shipment findById(Long id) {
        return shipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found: " + id));
    }

    public List<Shipment> findByOrder(Long orderId) {
        return shipmentRepository.findByOrder_Id(orderId);
    }

    public List<Shipment> findByStatus(ShipmentStatus status) {
        return shipmentRepository.findByStatus(status);
    }

    // Used by the incident-trigger scheduler (Milestone 5): shipments still
    // marked IN_TRANSIT past their expected delivery time are effectively late,
    // even before anyone manually flags them DELAYED.
    public List<Shipment> findOverdueInTransit() {
        return shipmentRepository.findByStatusAndExpectedDeliveryBefore(
                ShipmentStatus.IN_TRANSIT, LocalDateTime.now());
    }

    @Transactional
    public Shipment create(CreateShipmentRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found: " + request.getOrderId()));
        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Warehouse not found: " + request.getWarehouseId()));

        Shipment shipment = new Shipment();
        shipment.setOrder(order);
        shipment.setWarehouse(warehouse);
        shipment.setCourier(request.getCourier());
        shipment.setStatus(ShipmentStatus.PENDING);
        shipment.setExpectedDelivery(request.getExpectedDelivery());

        Shipment saved = shipmentRepository.save(shipment);
        // Re-fetch through the fetch-joined query so the returned object has
        // order/customer/items/product fully loaded instead of lazy proxies
        // that would otherwise fail to serialize once the transaction closes.
        return shipmentRepository.findById(saved.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found: " + saved.getId()));
    }

    @Transactional
    public Shipment dispatch(Long id) {
        Shipment shipment = findById(id);
        shipment.setStatus(ShipmentStatus.IN_TRANSIT);
        shipment.setDispatchedAt(LocalDateTime.now());
        return shipmentRepository.save(shipment);
    }

    @Transactional
    public Shipment markDelivered(Long id) {
        Shipment shipment = findById(id);
        shipment.setStatus(ShipmentStatus.DELIVERED);
        shipment.setDeliveredAt(LocalDateTime.now());
        return shipmentRepository.save(shipment);
    }

    @Transactional
    public Shipment markDelayed(Long id) {
        Shipment shipment = findById(id);
        shipment.setStatus(ShipmentStatus.DELAYED);
        return shipmentRepository.save(shipment);
    }
}