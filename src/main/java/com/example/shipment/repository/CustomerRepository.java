package com.example.shipment.repository;

import com.example.shipment.entity.Customer;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    boolean existsByEmail(String email);

    boolean existsByEmailAndCustomerIdNot(String email, Long customerId);

    // @EntityGraph = LEFT JOIN FETCH of the shipments: customers and their shipments
    // are read in ONE query instead of 1 + one query per customer (N+1).
    @Override
    @EntityGraph(attributePaths = "shipments")
    List<Customer> findAll();

    @Override
    @EntityGraph(attributePaths = "shipments")
    Optional<Customer> findById(Long customerId);
}
