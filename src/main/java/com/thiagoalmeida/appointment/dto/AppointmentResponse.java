package com.thiagoalmeida.appointment.dto;

import com.thiagoalmeida.appointment.model.AppointmentStatus;

import java.time.LocalDateTime;

public record AppointmentResponse(
        Long id,
        Long doctorId,
        Long patientId,
        LocalDateTime dateTime,
        String description,
        AppointmentStatus status,
        String doctorName,
        String patientName
) {}
