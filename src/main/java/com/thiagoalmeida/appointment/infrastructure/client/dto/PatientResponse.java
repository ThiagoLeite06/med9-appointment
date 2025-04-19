package com.thiagoalmeida.appointment.infrastructure.client.dto;

public record PatientResponse(
        Long id,
        String name,
        String cpf,
        String phone,
        String email
) {}
