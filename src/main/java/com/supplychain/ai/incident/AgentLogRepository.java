package com.supplychain.ai.incident;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AgentLogRepository extends JpaRepository<AgentLog, Long> {
    List<AgentLog> findByIncident_IdOrderByTimestampAsc(Long incidentId);
}
