package com.atendeai.modules.customer.dto;

import com.atendeai.modules.customer.model.Customer;

import java.time.Instant;
import java.util.UUID;

public record CustomerResponse(
        UUID id,
        String name,
        String email,
        String phone,
        String document,
        String address,
        String notes,
        Instant createdAt
) {
    public static CustomerResponse from(Customer c) {
        return new CustomerResponse(
                c.getId(), c.getName(), c.getEmail(), c.getPhone(),
                c.getDocument(), c.getAddress(), c.getNotes(), c.getCreatedAt());
    }
}
