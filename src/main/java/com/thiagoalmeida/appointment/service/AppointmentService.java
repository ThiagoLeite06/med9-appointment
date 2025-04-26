package com.thiagoalmeida.appointment.service;

import com.thiagoalmeida.appointment.dto.AppointmentRequest;
import com.thiagoalmeida.appointment.dto.AppointmentResponse;
import com.thiagoalmeida.appointment.model.AppointmentStatus;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentService {
    AppointmentResponse createAppointment(AppointmentRequest request);
    AppointmentResponse getAppointmentById(Long id);
    List<AppointmentResponse> getAppointmentsByPatientId(Long patientId);
    List<AppointmentResponse> getAppointmentsByDoctorId(Long doctorId);
    List<AppointmentResponse> getAppointmentsByDateRange(LocalDateTime start, LocalDateTime end);
    Page<AppointmentResponse> getAllAppointments(int page, int size);
    AppointmentResponse updateAppointment(Long id, AppointmentRequest request);
    AppointmentResponse updateAppointmentStatus(Long id, AppointmentStatus status);
}
