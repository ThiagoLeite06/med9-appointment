package com.thiagoalmeida.appointment.mapper;

import com.thiagoalmeida.appointment.dto.AppointmentRequest;
import com.thiagoalmeida.appointment.dto.AppointmentResponse;
import com.thiagoalmeida.appointment.model.Appointment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AppointmentMapper {

    Appointment toEntity(AppointmentRequest request);

    @Mapping(target = "doctorName", ignore = true)
    @Mapping(target = "patientName", ignore = true)
    AppointmentResponse toResponse(Appointment appointment);
}
