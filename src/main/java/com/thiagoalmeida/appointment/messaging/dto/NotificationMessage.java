package com.thiagoalmeida.appointment.messaging.dto;

import java.time.LocalDateTime;

public record NotificationMessage(
        Long appointmentId,
        Long patientId,
        Long doctorId,
        LocalDateTime dateTime,
        String message,
        String type
) {}
