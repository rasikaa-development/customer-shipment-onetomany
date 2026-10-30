package com.example.shipment.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "customer_name", nullable = false, length = 100)
    private String customerName;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    /**
     * ONE customer -> MANY shipments (inverse side).
     * mappedBy = "customer": the FK column shipments.customer_id is owned by
     * {@link Shipment#getCustomer()}, so no join table is created.
     * cascade = ALL + orphanRemoval: shipments are saved/deleted with their customer.
     */
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("shipmentId ASC")
    private List<Shipment> shipments = new ArrayList<>();

    protected Customer() {
        // required by JPA
    }

    public Customer(String customerName, String email) {
        this.customerName = customerName;
        this.email = email;
    }

    /** Always use this (not getShipments().add) so the owning side - the FK - is set too. */
    public void addShipment(Shipment shipment) {
        shipments.add(shipment);
        shipment.setCustomer(this);
    }

    /** With orphanRemoval = true, removing a shipment from the list deletes its row. */
    public void removeShipment(Shipment shipment) {
        shipments.remove(shipment);
        shipment.setCustomer(null);
    }

    public Long getCustomerId() { return customerId; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public List<Shipment> getShipments() { return shipments; }
}
