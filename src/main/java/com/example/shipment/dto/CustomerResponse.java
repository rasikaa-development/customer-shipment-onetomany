package com.example.shipment.dto;

import com.example.shipment.entity.Customer;
import com.example.shipment.entity.ShipmentStatus;
import java.util.List;

/** A customer with all of its shipments (one-to-many view). */
public record CustomerResponse(Long customerId, String customerName, String email, List<ShipmentItem> shipments) {

    public record ShipmentItem(Long shipmentId, String source, String destination, ShipmentStatus status) {
    }

    public static CustomerResponse from(Customer customer) {
        List<ShipmentItem> shipments = customer.getShipments().stream()
                .map(s -> new ShipmentItem(s.getShipmentId(), s.getSource(), s.getDestination(), s.getStatus()))
                .toList();
        return new CustomerResponse(customer.getCustomerId(), customer.getCustomerName(), customer.getEmail(), shipments);
    }
}
