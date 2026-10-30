package com.example.shipment.controller;

import com.example.shipment.dto.CustomerRequest;
import com.example.shipment.dto.CustomerResponse;
import com.example.shipment.dto.ShipmentRequest;
import com.example.shipment.dto.ShipmentResponse;
import com.example.shipment.service.CustomerService;
import com.example.shipment.service.ShipmentService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;
    private final ShipmentService shipmentService;

    public CustomerController(CustomerService customerService, ShipmentService shipmentService) {
        this.customerService = customerService;
        this.shipmentService = shipmentService;
    }

    /** Create a customer, optionally together with its shipments. */
    @PostMapping
    public ResponseEntity<CustomerResponse> createCustomer(@Valid @RequestBody CustomerRequest request) {
        CustomerResponse created = customerService.createCustomer(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(created.customerId()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    public List<CustomerResponse> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    @GetMapping("/{id}")
    public CustomerResponse getCustomer(@PathVariable Long id) {
        return customerService.getCustomer(id);
    }

    @PutMapping("/{id}")
    public CustomerResponse updateCustomer(@PathVariable Long id, @Valid @RequestBody CustomerRequest request) {
        return customerService.updateCustomer(id, request);
    }

    /** Delete a customer; all of its shipments are deleted by cascade. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }

    /** Add a shipment to an existing customer. */
    @PostMapping("/{id}/shipments")
    public ResponseEntity<ShipmentResponse> createShipment(@PathVariable Long id,
                                                           @Valid @RequestBody ShipmentRequest request) {
        ShipmentResponse created = shipmentService.createShipment(id, request);
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/shipments/{shipmentId}").buildAndExpand(created.shipmentId()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/{id}/shipments")
    public List<ShipmentResponse> getShipmentsOfCustomer(@PathVariable Long id) {
        return shipmentService.getShipmentsOfCustomer(id);
    }
}
