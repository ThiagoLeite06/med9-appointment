package com.thiagoalmeida.appointment.domain.usecase;

import com.thiagoalmeida.appointment.domain.entity.Appointment;

public interface UpdateAppointmentUseCase {
    Appointment execute(Appointment appointment);
}
