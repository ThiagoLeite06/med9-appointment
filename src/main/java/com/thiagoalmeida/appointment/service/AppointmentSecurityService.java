package com.thiagoalmeida.appointment.service;

import com.thiagoalmeida.appointment.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppointmentSecurityService {

    private final AppointmentRepository appointmentRepository;
    
    /**
     * Verifica se um paciente é o proprietário de uma consulta
     * 
     * @param appointmentId ID da consulta
     * @param patientId ID do paciente
     * @return true se o paciente for o proprietário da consulta, false caso contrário
     */
    public boolean isAppointmentOwner(Long appointmentId, Long patientId) {
        return appointmentRepository.findById(appointmentId)
                .map(appointment -> appointment.getPatientId().equals(patientId))
                .orElse(false);
    }
}
