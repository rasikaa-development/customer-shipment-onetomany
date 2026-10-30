package com.example.shipment.dto;

import com.example.shipment.entity.Customer;
import com.example.shipment.entity.Shipment;
import com.example.shipment.entity.ShipmentStatus;

/** A shipment with the one customer it belongs to (many-to-one view). */
public record ShipmentResponse(Long shipmentId, String source, String destination, ShipmentStatus status,
                               CustomerSummary customer) {

    public record CustomerSummary(Long customerId, String customerName, String email) {
    }

    public static ShipmentResponse from(Shipment shipment) {
        Customer customer = shipment.getCustomer();
        return new ShipmentResponse(shipment.getShipmentId(), shipment.getSource(), shipment.getDestination(),
                shipment.getStatus(),
                new CustomerSummary(customer.getCustomerId(), customer.getCustomerName(), customer.getEmail()));
    }
}
