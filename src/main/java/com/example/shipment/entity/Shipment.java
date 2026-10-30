package com.example.shipment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "shipments")
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shipment_id")
    private Long shipmentId;

    @Column(nullable = false, length = 100)
    private String source;

    @Column(nullable = false, length = 100)
    private String destination;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ShipmentStatus status;

    /**
     * MANY shipments -> ONE customer (owning side).
     * The FK column shipments.customer_id lives here; NOT NULL means every
     * shipment belongs to exactly one customer. LAZY: the customer is only
     * loaded when needed (or fetched explicitly with an entity graph).
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    protected Shipment() {
        // required by JPA
    }

    public Shipment(String source, String destination, ShipmentStatus status) {
        this.source = source;
        this.destination = destination;
        this.status = status;
    }

    public Long getShipmentId() { return shipmentId; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
    public ShipmentStatus getStatus() { return status; }
    public void setStatus(ShipmentStatus status) { this.status = status; }
    public Customer getCustomer() { return customer; }

    /** Package-private: use {@link Customer#addShipment(Shipment)} so both sides stay in sync. */
    void setCustomer(Customer customer) { this.customer = customer; }
}
