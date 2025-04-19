package com.thiagoalmeida.appointment.presentation.graphql;

import com.thiagoalmeida.appointment.application.dto.AppointmentRequest;
import com.thiagoalmeida.appointment.application.dto.AppointmentResponse;
import com.thiagoalmeida.appointment.domain.entity.Appointment;
import com.thiagoalmeida.appointment.domain.entity.AppointmentStatus;
import com.thiagoalmeida.appointment.domain.usecase.CreateAppointmentUseCase;
import com.thiagoalmeida.appointment.domain.usecase.GetAppointmentUseCase;
import com.thiagoalmeida.appointment.domain.usecase.UpdateAppointmentUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class AppointmentResolver {

    private final CreateAppointmentUseCase createAppointmentUseCase;
    private final GetAppointmentUseCase getAppointmentUseCase;
    private final UpdateAppointmentUseCase updateAppointmentUseCase;

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE') or (hasRole('PATIENT') and #patientId == authentication.principal.id)")
    public AppointmentResponse appointmentById(@Argument Long id) {
        return getAppointmentUseCase.execute(id)
                .map(this::mapToResponse)
                .orElse(null);
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE') or (hasRole('PATIENT') and #patientId == authentication.principal.id)")
    public List<AppointmentResponse> appointmentsByPatientId(@Argument Long patientId) {
        return getAppointmentUseCase.findByPatientId(patientId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE')")
    public List<AppointmentResponse> appointmentsByDoctorId(@Argument Long doctorId) {
        return getAppointmentUseCase.findByDoctorId(doctorId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE') or (hasRole('PATIENT') and #patientId == authentication.principal.id)")
    public List<AppointmentResponse> upcomingAppointmentsByPatientId(@Argument Long patientId) {
        LocalDateTime now = LocalDateTime.now();
        return getAppointmentUseCase.findByPatientId(patientId)
                .stream()
                .filter(a -> a.dateTime().isAfter(now))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE')")
    public List<AppointmentResponse> allAppointments() {
        return getAppointmentUseCase.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
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

        Appointment appointment = new Appointment(
                null,
                doctorId,
                patientId,
                dateTimeObj,
                description,
                status
        );

        return mapToResponse(createAppointmentUseCase.execute(appointment));
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

        Appointment currentAppointment = getAppointmentUseCase.execute(id)
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));

        LocalDateTime dateTimeObj = dateTime != null ?
                LocalDateTime.parse(dateTime, DateTimeFormatter.ISO_DATE_TIME) :
                currentAppointment.dateTime();

        Appointment updatedAppointment = new Appointment(
                id,
                doctorId != null ? doctorId : currentAppointment.doctorId(),
                patientId != null ? patientId : currentAppointment.patientId(),
                dateTimeObj,
                description != null ? description : currentAppointment.description(),
                status != null ? status : currentAppointment.status()
        );

        return mapToResponse(updateAppointmentUseCase.execute(updatedAppointment));
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE')")
    public AppointmentResponse cancelAppointment(@Argument Long id) {
        Appointment currentAppointment = getAppointmentUseCase.execute(id)
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));

        Appointment cancelledAppointment = new Appointment(
                id,
                currentAppointment.doctorId(),
                currentAppointment.patientId(),
                currentAppointment.dateTime(),
                currentAppointment.description(),
                AppointmentStatus.CANCELLED
        );

        return mapToResponse(updateAppointmentUseCase.execute(cancelledAppointment));
    }

    private AppointmentResponse mapToResponse(Appointment appointment) {
        // Aqui você pode fazer uma chamada REST para o serviço de usuários
        // para obter os nomes do médico e do paciente
        // Por enquanto, vamos apenas retornar os IDs
        return new AppointmentResponse(
                appointment.id(),
                appointment.doctorId(),
                appointment.patientId(),
                appointment.dateTime(),
                appointment.description(),
                appointment.status(),
                "Nome do médico", // Placeholder - substituir por chamada de serviço
                "Nome do paciente" // Placeholder - substituir por chamada de serviço
        );
    }
}