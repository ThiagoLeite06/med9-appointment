package com.thiagoalmeida.appointment.service;

import com.thiagoalmeida.appointment.dto.AppointmentRequest;
import com.thiagoalmeida.appointment.dto.AppointmentResponse;
import com.thiagoalmeida.appointment.mapper.AppointmentMapper;
import com.thiagoalmeida.appointment.model.Appointment;
import com.thiagoalmeida.appointment.model.AppointmentStatus;
import com.thiagoalmeida.appointment.repository.AppointmentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;

    public AppointmentServiceImpl(AppointmentRepository appointmentRepository,
                                  AppointmentMapper appointmentMapper
    ) {
        this.appointmentRepository = appointmentRepository;
        this.appointmentMapper = appointmentMapper;
    }

    @Override
    public AppointmentResponse createAppointment(AppointmentRequest request) {
        log.info("Creating appointment: {}", request);

        String token = getTokenFromSecurityContext();

        var appointment = appointmentMapper.toEntity(request);

        var savedAppointment = appointmentRepository.save(appointment);

        log.info("Notification sent for appointment: {}", savedAppointment.getId());

        return appointmentMapper.toResponse(savedAppointment);
    }

    @Override
    public AppointmentResponse getAppointmentById(String id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + id));

        return appointmentMapper.toResponse(appointment);
    }

    @Override
    public List<AppointmentResponse> getAppointmentsByPatientId(Long patientId) {
        return appointmentRepository.findByPatientId(patientId)
                .stream()
                .map(appointmentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentResponse> getAppointmentsByDoctorId(Long doctorId) {
        return appointmentRepository.findByDoctorId(doctorId)
                .stream()
                .map(appointmentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentResponse> getAppointmentsByDateRange(LocalDateTime start, LocalDateTime end) {
        return appointmentRepository.findByDateTimeBetween(start, end)
                .stream()
                .map(appointmentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<AppointmentResponse> getAllAppointments(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Appointment> appointments = appointmentRepository.findAll(pageable);
        return appointments.map(appointmentMapper::toResponse);
    }

    @Override
    public AppointmentResponse updateAppointment(String id, AppointmentRequest request) {
        var existingAppointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + id));

        var updatedAppointment = new Appointment(
                existingAppointment.getId(),
                request.doctorId(),
                request.patientId(),
                request.dateTime(),
                request.description(),
                request.status()
        );

        var savedAppointment = appointmentRepository.save(updatedAppointment);

        return appointmentMapper.toResponse(savedAppointment);
    }

    @Override
    @Transactional
    public AppointmentResponse updateAppointmentStatus(String id, AppointmentStatus status) {
        var existingAppointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + id));

        var updatedAppointment = new Appointment(
                existingAppointment.getId(),
                existingAppointment.getDoctorId(),
                existingAppointment.getPatientId(),
                existingAppointment.getDateTime(),
                existingAppointment.getDescription(),
                status
        );

        var savedAppointment = appointmentRepository.save(updatedAppointment);
        
        return appointmentMapper.toResponse(savedAppointment);
    }


    private String getTokenFromSecurityContext() {
        try {
            return SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();
        } catch (Exception e) {
            log.warn("Could not get token from security context, using default token");
            return "default-token";
        }
    }
}
