package com.thiagoalmeida.appointment.application.usecase;

import com.thiagoalmeida.appointment.application.dto.AppointmentRequest;
import com.thiagoalmeida.appointment.application.dto.AppointmentResponse;

public interface CreateAppointmentUseCase {
    AppointmentResponse execute(AppointmentRequest appointmentRequest);
}
