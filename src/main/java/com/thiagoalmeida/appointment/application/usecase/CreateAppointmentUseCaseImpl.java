package com.thiagoalmeida.appointment.application.usecase;

import com.thiagoalmeida.appointment.domain.entity.Appointment;
import com.thiagoalmeida.appointment.domain.repository.AppointmentRepository;
import com.thiagoalmeida.appointment.domain.usecase.CreateAppointmentUseCase;
import com.thiagoalmeida.appointment.infrastructure.messaging.NotificationPublisher;
import com.thiagoalmeida.appointment.infrastructure.messaging.dto.NotificationMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateAppointmentUseCaseImpl implements CreateAppointmentUseCase {

    private final AppointmentRepository appointmentRepository;
    private final NotificationPublisher notificationPublisher;

    @Override
    @Transactional
    public Appointment execute(Appointment appointment) {
        Appointment savedAppointment = appointmentRepository.save(appointment);

        NotificationMessage message = new NotificationMessage(
                savedAppointment.id(),
                savedAppointment.patientId(),
                savedAppointment.doctorId(),
                savedAppointment.dateTime(),
                "Uma nova consulta foi agendada",
                "APPOINTMENT_CREATED"
        );

        notificationPublisher.publishNotification(message);

        return savedAppointment;
    }
}
