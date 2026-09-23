package com.supplychain.ai.inventory;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    public List<Inventory> getAll() {
        return inventoryService.findAll();
    }

    @GetMapping("/{id}")
    public Inventory getById(@PathVariable Long id) {
        return inventoryService.findById(id);
    }

    // Core "tool" endpoint for the future Inventory Agent:
    // "check stock for this product across all warehouses"
    @GetMapping("/product/{productId}")
    public List<Inventory> getByProduct(@PathVariable Long productId) {
        return inventoryService.findByProduct(productId);
    }

    @GetMapping("/warehouse/{warehouseId}")
    public List<Inventory> getByWarehouse(@PathVariable Long warehouseId) {
        return inventoryService.findByWarehouse(warehouseId);
    }

    @PostMapping("/restock")
    public Inventory restock(@Valid @RequestBody InventoryActionRequest request) {
        return inventoryService.createOrRestock(
                request.getProductId(), request.getWarehouseId(), request.getQuantity());
    }

    // Future Execution Agent tool: reserve stock for an order.
    @PostMapping("/reserve")
    public Inventory reserve(@Valid @RequestBody InventoryActionRequest request) {
        return inventoryService.reserve(
                request.getProductId(), request.getWarehouseId(), request.getQuantity());
    }

    @PostMapping("/release")
    public Inventory release(@Valid @RequestBody InventoryActionRequest request) {
        return inventoryService.release(
                request.getProductId(), request.getWarehouseId(), request.getQuantity());
    }

    @PostMapping("/dispatch")
    public Inventory dispatch(@Valid @RequestBody InventoryActionRequest request) {
        return inventoryService.deductOnDispatch(
                request.getProductId(), request.getWarehouseId(), request.getQuantity());
    }
}
