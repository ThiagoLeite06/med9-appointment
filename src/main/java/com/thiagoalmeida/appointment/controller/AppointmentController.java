package com.thiagoalmeida.appointment.controller;

import com.thiagoalmeida.appointment.dto.AppointmentRequest;
import com.thiagoalmeida.appointment.dto.AppointmentResponse;
import com.thiagoalmeida.appointment.model.AppointmentStatus;
import com.thiagoalmeida.appointment.security.AppointmentSecurityService;
import com.thiagoalmeida.appointment.service.AppointmentService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final AppointmentSecurityService appointmentSecurityService;

    public AppointmentController(AppointmentService appointmentService, AppointmentSecurityService appointmentSecurityService) {
        this.appointmentService = appointmentService;
        this.appointmentSecurityService = appointmentSecurityService;
    }

    @PostMapping
    public ResponseEntity<AppointmentResponse> create(@RequestBody AppointmentRequest request) {
        return ResponseEntity.ok(appointmentService.createAppointment(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('NURSE') or (hasRole('PATIENT') and @appointmentSecurityService.isAppointmentOwner(#id, authentication.principal.username))")
    public ResponseEntity<AppointmentResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(appointmentService.getAppointmentById(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('DOCTOR') or hasRole('NURSE')")
    public ResponseEntity<Page<AppointmentResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(appointmentService.getAllAppointments(page, size));
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('NURSE') or (hasRole('PATIENT') and #patientId == authentication.principal.username)")
    public ResponseEntity<List<AppointmentResponse>> getByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByPatientId(patientId));
    }

    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('NURSE')")
    public ResponseEntity<List<AppointmentResponse>> getByDoctor(@PathVariable Long doctorId) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByDoctorId(doctorId));
    }

    @GetMapping("/range")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('NURSE')")
    public ResponseEntity<List<AppointmentResponse>> getByDateRange(
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByDateRange(start, end));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('NURSE')")
    public ResponseEntity<AppointmentResponse> update(@PathVariable String id, @RequestBody AppointmentRequest request) {
        return ResponseEntity.ok(appointmentService.updateAppointment(id, request));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('NURSE')")
    public ResponseEntity<AppointmentResponse> updateStatus(@PathVariable String id, @RequestBody AppointmentStatus status) {
        return ResponseEntity.ok(appointmentService.updateAppointmentStatus(id, status));
    }
}
