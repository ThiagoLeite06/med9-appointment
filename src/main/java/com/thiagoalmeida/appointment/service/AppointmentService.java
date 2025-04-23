package com.thiagoalmeida.appointment.service;

import com.thiagoalmeida.appointment.dto.AppointmentRequest;
import com.thiagoalmeida.appointment.dto.AppointmentResponse;
import com.thiagoalmeida.appointment.model.Appointment;
import com.thiagoalmeida.appointment.model.AppointmentStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentService {
    AppointmentResponse createAppointment(AppointmentRequest request);
    Optional<AppointmentResponse> getAppointmentById(Long id);
    List<AppointmentResponse> getAppointmentsByPatientId(Long patientId);
    List<AppointmentResponse> getAppointmentsByDoctorId(Long doctorId);
    List<AppointmentResponse> getAppointmentsByDateRange(LocalDateTime start, LocalDateTime end);
    List<AppointmentResponse> getAllAppointments();
    AppointmentResponse updateAppointment(Long id, AppointmentRequest request);
    AppointmentResponse updateAppointmentStatus(Long id, AppointmentStatus status);
}
