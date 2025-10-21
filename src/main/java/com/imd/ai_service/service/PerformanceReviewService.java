package com.imd.ai_service.service;

import com.imd.ai_service.dto.EmployeeDTO;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class PerformanceReviewService {

    private final ChatClient chatClient;
    private final WebClient.Builder webClientBuilder;
    private final String CRUD_SERVICE_URL = "http://crud-service";

    public PerformanceReviewService(ChatClient.Builder chatClientBuilder, WebClient.Builder webClientBuilder) {
        this.chatClient = chatClientBuilder.build();
        this.webClientBuilder = webClientBuilder;
    }

    public Mono<String> generateReview(Long employeeId) {
        // 1. Faz a chamada não-blocante para o crud-service para obter os dados do funcionário
        return webClientBuilder.build().get()
                .uri(CRUD_SERVICE_URL + "/employees/{id}", employeeId)
                .retrieve()
                .bodyToMono(EmployeeDTO.class)
                // 2. Se o funcionário não for encontrado, lança um erro reativo
                .switchIfEmpty(Mono.error(new RuntimeException("Employee not found with id: " + employeeId)))
                // 3. Usa flatMap para encadear a próxima operação assíncrona: a chamada à IA
                .flatMap(employee -> {
                    String promptMessage = String.format(
                            "Gere uma avaliação de desempenho hipotética e profissional para o funcionário a seguir. " +
                                    "A avaliação deve ter cerca de 3 parágrafos, destacando pontos fortes e áreas para desenvolvimento. " +
                                    "Nome do Funcionário: %s, Cargo: %s.",
                            employee.getName(), employee.getPosition()
                    );

                    String aiResponse = chatClient.prompt()
                            .user(promptMessage)
                            .call()
                            .content();

                    return Mono.just(aiResponse);
                });
    }
}

