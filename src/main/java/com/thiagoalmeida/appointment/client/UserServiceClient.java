package com.thiagoalmeida.appointment.client;

import com.thiagoalmeida.appointment.client.dto.DoctorResponse;
import com.thiagoalmeida.appointment.client.dto.PatientResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UserServiceClient {

    private final WebClient.Builder webClientBuilder;
    
    @Value("${user-service.url}")
    private String userServiceUrl;
    
    public Mono<DoctorResponse> getDoctorById(Long doctorId, String token) {
        return webClientBuilder.build()
                .get()
                .uri(userServiceUrl + "/doctors/{id}", doctorId)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(DoctorResponse.class)
                .onErrorResume(e -> Mono.empty());
    }
    
    public Mono<PatientResponse> getPatientById(Long patientId, String token) {
        return webClientBuilder.build()
                .get()
                .uri(userServiceUrl + "/patients/{id}", patientId)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(PatientResponse.class)
                .onErrorResume(e -> Mono.empty());
    }
}
