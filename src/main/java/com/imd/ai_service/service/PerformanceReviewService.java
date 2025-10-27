package com.imd.ai_service.service;

import com.imd.ai_service.client.CrudServiceClient;
import com.imd.ai_service.dto.EmployeeDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import feign.FeignException;

@Service
public class PerformanceReviewService {

    private static final Logger logger = LoggerFactory.getLogger(PerformanceReviewService.class);

    private final ChatClient chatClient;
    private final CrudServiceClient crudServiceClient;

    public PerformanceReviewService(ChatClient.Builder chatClientBuilder,
                                    CrudServiceClient crudServiceClient) {
        this.chatClient = chatClientBuilder.build();
        this.crudServiceClient = crudServiceClient;
    }

    public String generateReview(Long employeeId) {
        EmployeeDTO employee = null;
        try {
            employee = crudServiceClient.getEmployeeById(employeeId);

        } catch (FeignException.NotFound e) {
            return "Funcionário com ID " + employeeId + " não encontrado.";
        } catch (Exception e) {
            return "Erro ao buscar dados do funcionário com ID " + employeeId + ". Tente novamente mais tarde.";
        }

        if (employee == null) {
            return "Funcionário com ID " + employeeId + " não encontrado.";
        }

        if (employee.getName() == null || employee.getPosition() == null) {
            return "Erro: Dados do funcionário com ID " + employeeId + " estão incompletos.";
        }

        String promptMessage = String.format(
                "Gere uma avaliação de desempenho hipotética e profissional para o funcionário a seguir. " +
                        "A avaliação deve ter cerca de 3 parágrafos, destacando pontos fortes e áreas para desenvolvimento. " +
                        "Nome do Funcionário: %s, Cargo: %s.",
                employee.getName(), employee.getPosition()
        );

        try {

            return chatClient.prompt()
                    .user(promptMessage)
                    .call()
                    .content();

        } catch (Exception e) {
            return "Erro ao gerar a avaliação via IA para o funcionário com ID " + employeeId + ". Verifique se o serviço de IA está disponível.";
        }
    }
}