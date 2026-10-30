package com.example.shipment.controller;

import com.example.shipment.dto.ShipmentRequest;
import com.example.shipment.dto.ShipmentResponse;
import com.example.shipment.service.ShipmentService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Shipment operations; new shipments are created via POST /customers/{id}/shipments. */
@RestController
@RequestMapping("/shipments")
public class ShipmentController {

    private final ShipmentService shipmentService;

    public ShipmentController(ShipmentService shipmentService) {
        this.shipmentService = shipmentService;
    }

    @GetMapping
    public List<ShipmentResponse> getAllShipments() {
        return shipmentService.getAllShipments();
    }

    @GetMapping("/{id}")
    public ShipmentResponse getShipment(@PathVariable Long id) {
        return shipmentService.getShipment(id);
    }

    @PutMapping("/{id}")
    public ShipmentResponse updateShipment(@PathVariable Long id, @Valid @RequestBody ShipmentRequest request) {
        return shipmentService.updateShipment(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShipment(@PathVariable Long id) {
        shipmentService.deleteShipment(id);
        return ResponseEntity.noContent().build();
    }
}
