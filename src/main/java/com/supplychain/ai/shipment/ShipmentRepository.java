package com.supplychain.ai.shipment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ShipmentRepository extends JpaRepository<Shipment, Long> {

    String BASE_FETCH =
            "select distinct s from Shipment s " +
            "join fetch s.order o " +
            "join fetch s.warehouse " +
            "join fetch o.customer " +
            "left join fetch o.items i " +
            "left join fetch i.product ";

    @Query(BASE_FETCH)
    List<Shipment> findAll();

    @Query(BASE_FETCH + "where s.id = :id")
    Optional<Shipment> findById(@Param("id") Long id);

    @Query(BASE_FETCH + "where o.id = :orderId")
    List<Shipment> findByOrder_Id(@Param("orderId") Long orderId);

    @Query(BASE_FETCH + "where s.status = :status")
    List<Shipment> findByStatus(@Param("status") ShipmentStatus status);

    @Query(BASE_FETCH + "where s.status = :status and s.expectedDelivery < :cutoff")
    List<Shipment> findByStatusAndExpectedDeliveryBefore(@Param("status") ShipmentStatus status,
                                                          @Param("cutoff") LocalDateTime cutoff);
}