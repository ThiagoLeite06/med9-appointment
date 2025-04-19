package com.thiagoalmeida.appointment.infrastructure.messaging.dto;

import java.time.LocalDateTime;

public record NotificationMessage(
        Long appointmentId,
        Long patientId,
        Long doctorId,
        LocalDateTime appointmentDateTime,
        String message,
        String eventType
) {}
