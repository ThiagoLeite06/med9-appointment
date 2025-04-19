package com.thiagoalmeida.appointment.domain.entity;


import java.time.LocalDateTime;

public record Appointment(
        Long id,
        Long doctorId,
        Long patientId,
        LocalDateTime dateTime,
        String description,
        AppointmentStatus status
) {}
