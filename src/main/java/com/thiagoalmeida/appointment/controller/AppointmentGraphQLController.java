package com.thiagoalmeida.appointment.controller;

import com.thiagoalmeida.appointment.dto.AppointmentResponse;
import com.thiagoalmeida.appointment.model.Appointment;
import com.thiagoalmeida.appointment.model.AppointmentStatus;
import com.thiagoalmeida.appointment.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class AppointmentGraphQLController {

    private final AppointmentService appointmentService;

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE') or (hasRole('PATIENT') and #patientId == authentication.principal.id)")
    public AppointmentResponse appointmentById(@Argument Long id) {
        return appointmentService.getAppointmentById(id)
                .orElse(null);
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE') or (hasRole('PATIENT') and #patientId == authentication.principal.id)")
    public List<AppointmentResponse> appointmentsByPatientId(@Argument Long patientId) {
        return appointmentService.getAppointmentsByPatientId(patientId);
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE') or (hasRole('DOCTOR') and #doctorId == authentication.principal.id)")
    public List<AppointmentResponse> appointmentsByDoctorId(@Argument Long doctorId) {
        return appointmentService.getAppointmentsByDoctorId(doctorId);
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE') or (hasRole('PATIENT') and #patientId == authentication.principal.id)")
    public List<AppointmentResponse> upcomingAppointmentsByPatientId(@Argument Long patientId) {
        LocalDateTime now = LocalDateTime.now();
        return appointmentService.getAppointmentsByPatientId(patientId).stream()
                .filter(a -> a.dateTime().isAfter(now))
                .toList();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE')")
    public List<AppointmentResponse> allAppointments() {
        return appointmentService.getAllAppointments();
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE')")
    public AppointmentResponse createAppointment(
            @Argument Long doctorId,
            @Argument Long patientId,
            @Argument String dateTime,
            @Argument String description,
            @Argument AppointmentStatus status) {

        LocalDateTime dateTimeObj = LocalDateTime.parse(dateTime, DateTimeFormatter.ISO_DATE_TIME);

        return appointmentService.createAppointment(
                new com.thiagoalmeida.appointment.dto.AppointmentRequest(
                        doctorId,
                        patientId,
                        dateTimeObj,
                        description,
                        status
                )
        );
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE')")
    public AppointmentResponse updateAppointment(
            @Argument Long id,
            @Argument(required = false) Long doctorId,
            @Argument(required = false) Long patientId,
            @Argument(required = false) String dateTime,
            @Argument(required = false) String description,
            @Argument(required = false) AppointmentStatus status) {

        // Get current appointment
        AppointmentResponse currentAppointment = appointmentService.getAppointmentById(id)
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));

        // Parse dateTime if provided
        LocalDateTime dateTimeObj = dateTime != null ?
                LocalDateTime.parse(dateTime, DateTimeFormatter.ISO_DATE_TIME) :
                currentAppointment.dateTime();

        // Create request with updated fields
        com.thiagoalmeida.appointment.dto.AppointmentRequest request = new com.thiagoalmeida.appointment.dto.AppointmentRequest(
                doctorId != null ? doctorId : currentAppointment.doctorId(),
                patientId != null ? patientId : currentAppointment.patientId(),
                dateTimeObj,
                description != null ? description : currentAppointment.description(),
                status != null ? status : currentAppointment.status()
        );

        return appointmentService.updateAppointment(id, request);
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE') or (hasRole('PATIENT') and @appointmentSecurityService.isAppointmentOwner(#id, authentication.principal.id))")
    public AppointmentResponse cancelAppointment(@Argument Long id) {
        return appointmentService.updateAppointmentStatus(id, AppointmentStatus.CANCELLED);
    }
}
