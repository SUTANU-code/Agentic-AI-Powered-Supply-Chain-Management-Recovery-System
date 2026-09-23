package com.supplychain.ai.supplier;

import com.supplychain.ai.common.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplierProductRepository supplierProductRepository;

    public SupplierService(SupplierRepository supplierRepository,
                            SupplierProductRepository supplierProductRepository) {
        this.supplierRepository = supplierRepository;
        this.supplierProductRepository = supplierProductRepository;
    }

    public List<Supplier> findAll() {
        return supplierRepository.findAll();
    }

    public Supplier findById(Long id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found: " + id));
    }

    public Supplier create(Supplier supplier) {
        return supplierRepository.save(supplier);
    }

    public Supplier update(Long id, Supplier updated) {
        Supplier existing = findById(id);
        existing.setName(updated.getName());
        existing.setContactEmail(updated.getContactEmail());
        existing.setReliabilityScore(updated.getReliabilityScore());
        existing.setLeadTimeDays(updated.getLeadTimeDays());
        return supplierRepository.save(existing);
    }

    public void delete(Long id) {
        supplierRepository.delete(findById(id));
    }

    // Used later by the Supplier Agent to compare offers for a given product.
    public List<SupplierProduct> findOffersForProduct(Long productId) {
        return supplierProductRepository.findByProduct_Id(productId);
    }

    public SupplierProduct addOffer(SupplierProduct offer) {
        return supplierProductRepository.save(offer);
    }
}
