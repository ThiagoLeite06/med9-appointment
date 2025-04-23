package com.thiagoalmeida.appointment.service;

import com.thiagoalmeida.appointment.client.UserServiceClient;
import com.thiagoalmeida.appointment.client.dto.DoctorResponse;
import com.thiagoalmeida.appointment.client.dto.PatientResponse;
import com.thiagoalmeida.appointment.dto.AppointmentRequest;
import com.thiagoalmeida.appointment.dto.AppointmentResponse;
import com.thiagoalmeida.appointment.messaging.NotificationPublisher;
import com.thiagoalmeida.appointment.messaging.dto.NotificationMessage;
import com.thiagoalmeida.appointment.model.Appointment;
import com.thiagoalmeida.appointment.model.AppointmentStatus;
import com.thiagoalmeida.appointment.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserServiceClient userServiceClient;
    private final NotificationPublisher notificationPublisher;

    @Override
    @Transactional
    public AppointmentResponse createAppointment(AppointmentRequest request) {
        log.info("Creating appointment: {}", request);

        // Get token from security context
        String token = getTokenFromSecurityContext();

        // Get doctor and patient information from the user service
        Mono<DoctorResponse> doctorResponseMono = userServiceClient.getDoctorById(
                request.doctorId(),
                token
        );

        Mono<PatientResponse> patientResponseMono = userServiceClient.getPatientById(
                request.patientId(),
                token
        );

        // Map DTO to entity
        Appointment appointment = new Appointment(
                null,
                request.doctorId(),
                request.patientId(),
                request.dateTime(),
                request.description(),
                request.status()
        );

        // Save the appointment
        Appointment savedAppointment = appointmentRepository.save(appointment);

        // Create notification message
        NotificationMessage notificationMessage = new NotificationMessage(
                savedAppointment.getId(),
                savedAppointment.getPatientId(),
                savedAppointment.getDoctorId(),
                savedAppointment.getDateTime(),
                "Nova consulta agendada para " + savedAppointment.getDateTime().toString(),
                "APPOINTMENT_CREATED"
        );

        // Publish notification
        notificationPublisher.publishNotification(notificationMessage);
        log.info("Notification sent for appointment: {}", savedAppointment.getId());

        // Get doctor and patient names for the response
        DoctorResponse doctorResponse = doctorResponseMono.block();
        PatientResponse patientResponse = patientResponseMono.block();

        // Map entity to response DTO
        return mapToResponse(savedAppointment, doctorResponse, patientResponse);
    }

    @Override
    public Optional<AppointmentResponse> getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .map(this::enrichAppointmentWithUserDetails);
    }

    @Override
    public List<AppointmentResponse> getAppointmentsByPatientId(Long patientId) {
        return appointmentRepository.findByPatientId(patientId)
                .stream()
                .map(this::enrichAppointmentWithUserDetails)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentResponse> getAppointmentsByDoctorId(Long doctorId) {
        return appointmentRepository.findByDoctorId(doctorId)
                .stream()
                .map(this::enrichAppointmentWithUserDetails)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentResponse> getAppointmentsByDateRange(LocalDateTime start, LocalDateTime end) {
        return appointmentRepository.findByDateTimeBetween(start, end)
                .stream()
                .map(this::enrichAppointmentWithUserDetails)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentResponse> getAllAppointments() {
        return appointmentRepository.findAll()
                .stream()
                .map(this::enrichAppointmentWithUserDetails)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AppointmentResponse updateAppointment(Long id, AppointmentRequest request) {
        Appointment existingAppointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + id));

        Appointment updatedAppointment = new Appointment(
                existingAppointment.getId(),
                request.doctorId(),
                request.patientId(),
                request.dateTime(),
                request.description(),
                request.status()
        );

        Appointment savedAppointment = appointmentRepository.save(updatedAppointment);
        
        // Send notification about update
        NotificationMessage notificationMessage = new NotificationMessage(
                savedAppointment.getId(),
                savedAppointment.getPatientId(),
                savedAppointment.getDoctorId(),
                savedAppointment.getDateTime(),
                "Consulta atualizada para " + savedAppointment.getDateTime().toString(),
                "APPOINTMENT_UPDATED"
        );
        
        notificationPublisher.publishNotification(notificationMessage);
        
        return enrichAppointmentWithUserDetails(savedAppointment);
    }

    @Override
    @Transactional
    public AppointmentResponse updateAppointmentStatus(Long id, AppointmentStatus status) {
        Appointment existingAppointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + id));

        Appointment updatedAppointment = new Appointment(
                existingAppointment.getId(),
                existingAppointment.getDoctorId(),
                existingAppointment.getPatientId(),
                existingAppointment.getDateTime(),
                existingAppointment.getDescription(),
                status
        );

        Appointment savedAppointment = appointmentRepository.save(updatedAppointment);
        
        // Send notification about status change
        NotificationMessage notificationMessage = new NotificationMessage(
                savedAppointment.getId(),
                savedAppointment.getPatientId(),
                savedAppointment.getDoctorId(),
                savedAppointment.getDateTime(),
                "Status da consulta alterado para " + status,
                "APPOINTMENT_STATUS_CHANGED"
        );
        
        notificationPublisher.publishNotification(notificationMessage);
        
        return enrichAppointmentWithUserDetails(savedAppointment);
    }

    private AppointmentResponse enrichAppointmentWithUserDetails(Appointment appointment) {
        String token = getTokenFromSecurityContext();
        
        DoctorResponse doctorResponse = userServiceClient.getDoctorById(
                appointment.getDoctorId(), token).block();
        
        PatientResponse patientResponse = userServiceClient.getPatientById(
                appointment.getPatientId(), token).block();
        
        return mapToResponse(appointment, doctorResponse, patientResponse);
    }
    
    private AppointmentResponse mapToResponse(
            Appointment appointment, 
            DoctorResponse doctorResponse, 
            PatientResponse patientResponse) {
        
        return new AppointmentResponse(
                appointment.getId(),
                appointment.getDoctorId(),
                appointment.getPatientId(),
                appointment.getDateTime(),
                appointment.getDescription(),
                appointment.getStatus(),
                doctorResponse != null ? doctorResponse.name() : "Unknown Doctor",
                patientResponse != null ? patientResponse.name() : "Unknown Patient"
        );
    }
    
    private String getTokenFromSecurityContext() {
        try {
            // This is a simplified approach. In a real application, you would extract the token properly
            // from the security context based on your authentication mechanism
            return SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();
        } catch (Exception e) {
            log.warn("Could not get token from security context, using default token");
            return "default-token"; // Fallback for development/testing
        }
    }
}
