package com.imd.ai_service.service;

import com.imd.ai_service.dto.EmployeeDTO;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.stream.Collectors;

@Service
public class PerformanceReviewService {

    private final ChatClient chatClient;
    private final WebClient.Builder webClientBuilder;
    private final String CRUD_SERVICE_URL = "http://crud-service";

    public PerformanceReviewService(ChatClient chatClient, WebClient.Builder webClientBuilder) {
        this.chatClient = chatClient;
        this.webClientBuilder = webClientBuilder;
    }

    public Mono<String> generateReview(Long employeeId) {
        // Chamada reativa ao crud-service (executa no event-loop)
        return webClientBuilder.build().get()
                .uri(CRUD_SERVICE_URL + "/employees/{id}", employeeId)
                .retrieve()
                .bodyToMono(EmployeeDTO.class)
                .switchIfEmpty(Mono.error(new RuntimeException("Employee not found with id: " + employeeId)))

                .flatMap(employee -> {
                    String promptMessage = String.format(
                            "Gere uma avaliação de desempenho hipotética e profissional para o funcionário a seguir. " +
                                    "A avaliação deve ter cerca de 3 parágrafos, destacando pontos fortes e áreas para desenvolvimento. " +
                                    "Nome do Funcionário: %s, Cargo: %s.",
                            employee.getName(), employee.getPosition()
                    );

                    Flux<String> aiCallFlux = chatClient.prompt()
                            .user(promptMessage)
                            .stream()
                            .content();

                    return aiCallFlux
                            .collect(Collectors.joining())
                            .publishOn(Schedulers.boundedElastic());
                });
    }
}

