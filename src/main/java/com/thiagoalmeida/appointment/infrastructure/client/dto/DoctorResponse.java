package com.thiagoalmeida.appointment.infrastructure.client.dto;

public record DoctorResponse(
        Long id,
        String name,
        String crm,
        String specialty,
        String phone,
        String email
) {}
