package com.example.shipment.repository;

import com.example.shipment.entity.Shipment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShipmentRepository extends JpaRepository<Shipment, Long> {

    // the customer (many-to-one side) is fetched in the same query
    @Override
    @EntityGraph(attributePaths = "customer")
    List<Shipment> findAll();

    @Override
    @EntityGraph(attributePaths = "customer")
    Optional<Shipment> findById(Long shipmentId);

    /** Derived query: WHERE shipment.customer.customerId = ? */
    @EntityGraph(attributePaths = "customer")
    List<Shipment> findByCustomerCustomerIdOrderByShipmentIdAsc(Long customerId);
}
