package com.example.shipment.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * Body of POST / PUT /customers.
 * {@code shipments} is optional and only used by POST (to create shipments with the customer);
 * PUT updates customer details only.
 */
public record CustomerRequest(
        @NotBlank @Size(max = 100) String customerName,
        @NotBlank @Email @Size(max = 150) String email,
        List<@NotNull @Valid ShipmentRequest> shipments) {
}
