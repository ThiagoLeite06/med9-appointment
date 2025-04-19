package com.thiagoalmeida.appointment.infrastructure.client;

import com.thiagoalmeida.appointment.infrastructure.client.dto.DoctorResponse;
import com.thiagoalmeida.appointment.infrastructure.client.dto.PatientResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class UserServiceClient {

    private final WebClient webClient;

    public UserServiceClient(@Value("${user-service.url}") String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public Mono<DoctorResponse> getDoctorById(Long doctorId, String token) {
        return webClient.get()
                .uri("/doctors/{id}", doctorId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(DoctorResponse.class);
    }

    public Mono<PatientResponse> getPatientById(Long patientId, String token) {
        return webClient.get()
                .uri("/patients/{id}", patientId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(PatientResponse.class);
    }
}