package com.supplychain.ai.supplier;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupplierProductRepository extends JpaRepository<SupplierProduct, Long> {

    // Used later by the Supplier Agent: "which suppliers can provide this product,
    // and at what price/lead time?"
    List<SupplierProduct> findByProduct_Id(Long productId);
}
