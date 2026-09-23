package com.supplychain.ai.incident;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IncidentRepository extends JpaRepository<Incident, Long> {
    List<Incident> findByStatus(IncidentStatus status);
    List<Incident> findByType(IncidentType type);

    // Prevents duplicate incident creation for the same order (Milestone 5 trigger logic).
    Optional<Incident> findByRelatedOrder_IdAndTypeAndStatusNot(
            Long orderId, IncidentType type, IncidentStatus excludedStatus);
}
