package com.supplychain.ai.incident;

import jakarta.validation.Valid;
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

import java.util.List;

@RestController
@RequestMapping("/api/incidents")
public class IncidentController {

    private final IncidentService incidentService;

    public IncidentController(IncidentService incidentService) {
        this.incidentService = incidentService;
    }

    @GetMapping
    public List<Incident> getAll(
            @RequestParam(required = false) IncidentStatus status,
            @RequestParam(required = false) IncidentType type) {
        if (status != null) return incidentService.findByStatus(status);
        if (type != null) return incidentService.findByType(type);
        return incidentService.findAll();
    }

    @GetMapping("/{id}")
    public Incident getById(@PathVariable Long id) {
        return incidentService.findById(id);
    }

    // Called by the Milestone 5 trigger scheduler, and later by agents
    // that detect problems independently.
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Incident create(@Valid @RequestBody CreateIncidentRequest request) {
        return incidentService.createIfNotDuplicate(request);
    }

    @PatchMapping("/{id}/status")
    public Incident updateStatus(@PathVariable Long id, @RequestParam IncidentStatus status) {
        return incidentService.updateStatus(id, status);
    }

    // --- Agent activity trail (Milestone 9 UI, but usable from Milestone 6 onward) ---

    @GetMapping("/{id}/logs")
    public List<AgentLog> getLogs(@PathVariable Long id) {
        return incidentService.getLogs(id);
    }

    @PostMapping("/{id}/logs")
    @ResponseStatus(HttpStatus.CREATED)
    public AgentLog addLog(@PathVariable Long id, @Valid @RequestBody AddAgentLogRequest request) {
        return incidentService.addLog(id, request.getAgentName(), request.getAction(), request.getMessage());
    }
}
