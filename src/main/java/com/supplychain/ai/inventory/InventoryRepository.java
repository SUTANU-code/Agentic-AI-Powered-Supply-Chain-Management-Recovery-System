package com.supplychain.ai.inventory;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    @Query("select i from Inventory i join fetch i.product join fetch i.warehouse")
    List<Inventory> findAll();

    @Query("select i from Inventory i join fetch i.product join fetch i.warehouse where i.id = :id")
    Optional<Inventory> findById(@Param("id") Long id);

    // Used later by the Inventory Agent: "check this product across all warehouses"
    @Query("select i from Inventory i join fetch i.product join fetch i.warehouse where i.product.id = :productId")
    List<Inventory> findByProduct_Id(@Param("productId") Long productId);

    @Query("select i from Inventory i join fetch i.product join fetch i.warehouse " +
            "where i.product.id = :productId and i.warehouse.id = :warehouseId")
    Optional<Inventory> findByProduct_IdAndWarehouse_Id(@Param("productId") Long productId,
                                                         @Param("warehouseId") Long warehouseId);

    @Query("select i from Inventory i join fetch i.product join fetch i.warehouse where i.warehouse.id = :warehouseId")
    List<Inventory> findByWarehouse_Id(@Param("warehouseId") Long warehouseId);
}