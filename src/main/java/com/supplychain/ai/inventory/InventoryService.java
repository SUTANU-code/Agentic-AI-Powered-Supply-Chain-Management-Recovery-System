package com.supplychain.ai.inventory;

import com.supplychain.ai.common.BadRequestException;
import com.supplychain.ai.common.ResourceNotFoundException;
import com.supplychain.ai.product.Product;
import com.supplychain.ai.product.ProductRepository;
import com.supplychain.ai.warehouse.Warehouse;
import com.supplychain.ai.warehouse.WarehouseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;

    public InventoryService(InventoryRepository inventoryRepository,
                             ProductRepository productRepository,
                             WarehouseRepository warehouseRepository) {
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
        this.warehouseRepository = warehouseRepository;
    }

    public List<Inventory> findAll() {
        return inventoryRepository.findAll();
    }

    public Inventory findById(Long id) {
        return inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory record not found: " + id));
    }

    // Future Inventory Agent tool: GET /api/inventory/product/{productId}
    // "check this product's stock across every warehouse"
    public List<Inventory> findByProduct(Long productId) {
        return inventoryRepository.findByProduct_Id(productId);
    }

    public List<Inventory> findByWarehouse(Long warehouseId) {
        return inventoryRepository.findByWarehouse_Id(warehouseId);
    }

    @Transactional
    public Inventory createOrRestock(Long productId, Long warehouseId, int quantityToAdd) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found: " + warehouseId));

        Inventory inventory = inventoryRepository
                .findByProduct_IdAndWarehouse_Id(productId, warehouseId)
                .orElseGet(() -> Inventory.of(product, warehouse));

        inventory.setQuantity(inventory.getQuantity() + quantityToAdd);
        return inventoryRepository.save(inventory);
    }

    // Future Execution Agent tool: POST /api/inventory/reserve
    // Called when an agent decides to fulfill an order from a specific warehouse.
    @Transactional
    public Inventory reserve(Long productId, Long warehouseId, int quantity) {
        Inventory inventory = inventoryRepository
                .findByProduct_IdAndWarehouse_Id(productId, warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No inventory record for product " + productId + " at warehouse " + warehouseId));

        if (inventory.getAvailableQuantity() < quantity) {
            throw new BadRequestException(
                    "Insufficient available stock: requested " + quantity +
                            ", available " + inventory.getAvailableQuantity());
        }

        inventory.setReservedQuantity(inventory.getReservedQuantity() + quantity);
        return inventoryRepository.save(inventory);
    }

    // Releases a reservation, e.g. if an order is cancelled before shipment.
    @Transactional
    public Inventory release(Long productId, Long warehouseId, int quantity) {
        Inventory inventory = inventoryRepository
                .findByProduct_IdAndWarehouse_Id(productId, warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No inventory record for product " + productId + " at warehouse " + warehouseId));

        int newReserved = Math.max(0, inventory.getReservedQuantity() - quantity);
        inventory.setReservedQuantity(newReserved);
        return inventoryRepository.save(inventory);
    }

    // Called when a shipment actually leaves the warehouse: physical stock
    // and the reservation both drop together.
    @Transactional
    public Inventory deductOnDispatch(Long productId, Long warehouseId, int quantity) {
        Inventory inventory = inventoryRepository
                .findByProduct_IdAndWarehouse_Id(productId, warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No inventory record for product " + productId + " at warehouse " + warehouseId));

        if (inventory.getQuantity() < quantity) {
            throw new BadRequestException("Cannot dispatch more than physically in stock");
        }

        inventory.setQuantity(inventory.getQuantity() - quantity);
        inventory.setReservedQuantity(Math.max(0, inventory.getReservedQuantity() - quantity));
        return inventoryRepository.save(inventory);
    }
}
