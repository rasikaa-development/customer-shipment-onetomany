package com.example.shipment.dto;

import com.example.shipment.entity.Shipment;
import com.example.shipment.entity.ShipmentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** {@code status} is optional: CREATED on create, unchanged on update. */
public record ShipmentRequest(
        @NotBlank @Size(max = 100) String source,
        @NotBlank @Size(max = 100) String destination,
        ShipmentStatus status) {

    public Shipment toEntity() {
        return new Shipment(source, destination, status == null ? ShipmentStatus.CREATED : status);
    }
}
