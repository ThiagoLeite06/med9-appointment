package com.thiagoalmeida.appointment.domain.usecase;

import com.thiagoalmeida.appointment.domain.entity.Appointment;

public interface CreateAppointmentUseCase {
    Appointment execute(Appointment appointment);
}
