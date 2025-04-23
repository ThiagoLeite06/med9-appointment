package com.thiagoalmeida.appointment.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Document(collection = "appointments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {

    @Id
    private String id;

    private Long doctorId;

    private Long patientId;

    private LocalDateTime dateTime;

    private String description;

    private AppointmentStatus status;
}