package com.thiagoalmeida.appointment.config;

import com.thiagoalmeida.appointment.client.dto.DoctorResponse;
import com.thiagoalmeida.appointment.client.dto.PatientResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;

/**
 * Configuração que cria endpoints mock para o serviço de usuários.
 * Ative com a propriedade app.mock-user-service=true
 */
@Configuration
@ConditionalOnProperty(name = "app.mock-user-service", havingValue = "true")
public class MockUserServiceConfig {

    private final Map<Long, DoctorResponse> doctors = new ConcurrentHashMap<>();
    private final Map<Long, PatientResponse> patients = new ConcurrentHashMap<>();

    public MockUserServiceConfig() {
        // Pré-carrega alguns dados mock
        doctors.put(1L, new DoctorResponse(1L, "Dr. João Silva", "Cardiologia", "joao.silva@example.com"));
        doctors.put(2L, new DoctorResponse(2L, "Dra. Maria Santos", "Dermatologia", "maria.santos@example.com"));
        doctors.put(3L, new DoctorResponse(3L, "Dr. Carlos Oliveira", "Ortopedia", "carlos.oliveira@example.com"));

        patients.put(1L, new PatientResponse(1L, "Ana Pereira", "ana.pereira@example.com", "11987654321"));
        patients.put(2L, new PatientResponse(2L, "Pedro Souza", "pedro.souza@example.com", "11976543210"));
        patients.put(3L, new PatientResponse(3L, "Lucia Ferreira", "lucia.ferreira@example.com", "11965432109"));
    }

    @Bean
    @Primary
    public RouterFunction<ServerResponse> mockUserServiceRoutes() {
        return RouterFunctions
                .route(GET("/api/doctors/{id}"), request -> {
                    Long id = Long.parseLong(request.pathVariable("id"));
                    if (doctors.containsKey(id)) {
                        return ServerResponse.ok().bodyValue(doctors.get(id));
                    }
                    return ServerResponse.notFound().build();
                })
                .andRoute(GET("/api/patients/{id}"), request -> {
                    Long id = Long.parseLong(request.pathVariable("id"));
                    if (patients.containsKey(id)) {
                        return ServerResponse.ok().bodyValue(patients.get(id));
                    }
                    return ServerResponse.notFound().build();
                });
    }
}
