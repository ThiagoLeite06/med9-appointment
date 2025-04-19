package com.thiagoalmeida.appointment.application.dto;

import com.thiagoalmeida.appointment.domain.entity.AppointmentStatus;

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