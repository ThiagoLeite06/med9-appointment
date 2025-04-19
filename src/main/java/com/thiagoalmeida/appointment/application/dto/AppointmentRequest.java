package com.thiagoalmeida.appointment.application.dto;

import com.thiagoalmeida.appointment.domain.entity.AppointmentStatus;

import java.time.LocalDateTime;

public record AppointmentRequest(
        Long doctorId,
        Long patientId,
        LocalDateTime dateTime,
        String description,
        AppointmentStatus status
) {}