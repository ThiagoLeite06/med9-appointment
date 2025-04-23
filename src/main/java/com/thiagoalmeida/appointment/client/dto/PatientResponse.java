package com.thiagoalmeida.appointment.client.dto;

public record PatientResponse(
        Long id,
        String name,
        String email,
        String phone
) {}
