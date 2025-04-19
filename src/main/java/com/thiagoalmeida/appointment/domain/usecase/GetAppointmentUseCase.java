package com.thiagoalmeida.appointment.domain.usecase;

import com.thiagoalmeida.appointment.domain.entity.Appointment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface GetAppointmentUseCase {
    Optional<Appointment> execute(Long id);
    List<Appointment> findByPatientId(Long patientId);
    List<Appointment> findByDoctorId(Long doctorId);
    List<Appointment> findByDateTimeBetween(LocalDateTime start, LocalDateTime end);
    List<Appointment> findAll();
}
