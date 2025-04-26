//package com.thiagoalmeida.appointment.security;
//
//import com.thiagoalmeida.appointment.repository.AppointmentRepository;
//import org.springframework.stereotype.Service;
//
//@Service
//public class AppointmentSecurityService {
//    private final AppointmentRepository appointmentRepository;
//
//    public AppointmentSecurityService(AppointmentRepository appointmentRepository) {
//        this.appointmentRepository = appointmentRepository;
//    }
//
//    public boolean isAppointmentOwner(String appointmentId, String username) {
//        return appointmentRepository.findById(appointmentId)
//                .map(app -> app.getPatientId().toString().equals(username))
//                .orElse(false);
//    }
//}
