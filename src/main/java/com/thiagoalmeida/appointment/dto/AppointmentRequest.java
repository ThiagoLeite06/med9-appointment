package com.thiagoalmeida.appointment.dto;

import com.thiagoalmeida.appointment.model.AppointmentStatus;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record AppointmentRequest(
        @NotNull Long doctorId,
        @NotNull Long patientId,
        @NotNull @Future LocalDateTime dateTime,
        String description,
        @NotNull AppointmentStatus status
) {}
