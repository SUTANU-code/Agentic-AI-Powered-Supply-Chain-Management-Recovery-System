package com.supplychain.ai.incident;

import com.supplychain.ai.common.ResourceNotFoundException;
import com.supplychain.ai.order.Order;
import com.supplychain.ai.order.OrderRepository;
import com.supplychain.ai.warehouse.Warehouse;
import com.supplychain.ai.warehouse.WarehouseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class IncidentService {

    private final IncidentRepository incidentRepository;
    private final AgentLogRepository agentLogRepository;
    private final OrderRepository orderRepository;
    private final WarehouseRepository warehouseRepository;

    public IncidentService(IncidentRepository incidentRepository,
                            AgentLogRepository agentLogRepository,
                            OrderRepository orderRepository,
                            WarehouseRepository warehouseRepository) {
        this.incidentRepository = incidentRepository;
        this.agentLogRepository = agentLogRepository;
        this.orderRepository = orderRepository;
        this.warehouseRepository = warehouseRepository;
    }

    public List<Incident> findAll() {
        return incidentRepository.findAll();
    }

    public Incident findById(Long id) {
        return incidentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Incident not found: " + id));
    }

    public List<Incident> findByStatus(IncidentStatus status) {
        return incidentRepository.findByStatus(status);
    }

    public List<Incident> findByType(IncidentType type) {
        return incidentRepository.findByType(type);
    }

    // Used by both the Milestone 5 trigger scheduler and, later, agents that
    // detect a problem themselves. Avoids creating a duplicate OPEN/INVESTIGATING
    // incident of the same type for the same order.
    @Transactional
    public Incident createIfNotDuplicate(CreateIncidentRequest request) {
        Order order = null;
        if (request.getRelatedOrderId() != null) {
            order = orderRepository.findById(request.getRelatedOrderId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Order not found: " + request.getRelatedOrderId()));

            Optional<Incident> existing = incidentRepository
                    .findByRelatedOrder_IdAndTypeAndStatusNot(
                            request.getRelatedOrderId(), request.getType(), IncidentStatus.RESOLVED);
            if (existing.isPresent()) {
                return existing.get();
            }
        }

        Warehouse warehouse = null;
        if (request.getRelatedWarehouseId() != null) {
            warehouse = warehouseRepository.findById(request.getRelatedWarehouseId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Warehouse not found: " + request.getRelatedWarehouseId()));
        }

        Incident incident = Incident.create(request.getType(), order, warehouse, request.getDescription());

        return incidentRepository.save(incident);
    }

    @Transactional
    public Incident updateStatus(Long id, IncidentStatus status) {
        Incident incident = findById(id);
        incident.setStatus(status);
        if (status == IncidentStatus.RESOLVED || status == IncidentStatus.FAILED) {
            incident.setResolvedAt(LocalDateTime.now());
        }
        return incidentRepository.save(incident);
    }

    // Future agent tool: append a step to an incident's activity trail.
    @Transactional
    public AgentLog addLog(Long incidentId, String agentName, String action, String message) {
        Incident incident = findById(incidentId);
        AgentLog log = new AgentLog();
        log.setIncident(incident);
        log.setAgentName(agentName);
        log.setAction(action);
        log.setMessage(message);
        return agentLogRepository.save(log);
    }

    public List<AgentLog> getLogs(Long incidentId) {
        return agentLogRepository.findByIncident_IdOrderByTimestampAsc(incidentId);
    }
}
