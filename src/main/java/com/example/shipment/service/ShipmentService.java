package com.example.shipment.service;

import com.example.shipment.dto.ShipmentRequest;
import com.example.shipment.dto.ShipmentResponse;
import com.example.shipment.entity.Customer;
import com.example.shipment.entity.Shipment;
import com.example.shipment.exception.ResourceNotFoundException;
import com.example.shipment.repository.CustomerRepository;
import com.example.shipment.repository.ShipmentRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final CustomerRepository customerRepository;

    public ShipmentService(ShipmentRepository shipmentRepository, CustomerRepository customerRepository) {
        this.shipmentRepository = shipmentRepository;
        this.customerRepository = customerRepository;
    }

    /** Creates a shipment for an existing customer. */
    public ShipmentResponse createShipment(Long customerId, ShipmentRequest request) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id " + customerId));
        Shipment shipment = request.toEntity();
        customer.addShipment(shipment);
        return ShipmentResponse.from(shipmentRepository.save(shipment)); // INSERT with customer_id
    }

    @Transactional(readOnly = true)
    public List<ShipmentResponse> getShipmentsOfCustomer(Long customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found with id " + customerId);
        }
        return shipmentRepository.findByCustomerCustomerIdOrderByShipmentIdAsc(customerId).stream()
                .map(ShipmentResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<ShipmentResponse> getAllShipments() {
        return shipmentRepository.findAll().stream().map(ShipmentResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ShipmentResponse getShipment(Long shipmentId) {
        return ShipmentResponse.from(findShipment(shipmentId));
    }

    public ShipmentResponse updateShipment(Long shipmentId, ShipmentRequest request) {
        Shipment shipment = findShipment(shipmentId);
        shipment.setSource(request.source());
        shipment.setDestination(request.destination());
        if (request.status() != null) {
            shipment.setStatus(request.status());
        }
        return ShipmentResponse.from(shipment);
    }

    public void deleteShipment(Long shipmentId) {
        Shipment shipment = findShipment(shipmentId);
        // removing it from the customer's list deletes the row (orphanRemoval = true)
        shipment.getCustomer().removeShipment(shipment);
    }

    private Shipment findShipment(Long shipmentId) {
        return shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found with id " + shipmentId));
    }
}
