package com.thiagoalmeida.appointment.dto;

import com.thiagoalmeida.appointment.model.AppointmentStatus;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record AppointmentRequest(
        Long doctorId,
        Long patientId,
        LocalDateTime dateTime,
        String description,
        AppointmentStatus status
) {}
