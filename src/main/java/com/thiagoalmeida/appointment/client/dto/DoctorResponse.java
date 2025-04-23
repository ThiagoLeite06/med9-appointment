package com.thiagoalmeida.appointment.client.dto;

public record DoctorResponse(
        Long id,
        String name,
        String specialty,
        String email
) {}
