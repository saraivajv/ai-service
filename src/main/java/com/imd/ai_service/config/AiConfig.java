package com.imd.ai_service.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.model.ollama.autoconfigure.OllamaChatProperties;
import org.springframework.ai.model.ollama.autoconfigure.OllamaConnectionProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AiConfig {

    @Bean
    public ChatClient chatClient(ChatModel chatModel) {
        return ChatClient.builder(chatModel).build();
    }

    @Bean
    public OllamaChatModel ollamaChatModel(
            OllamaChatProperties chatProperties,
            OllamaConnectionProperties connectionProperties,
            @Qualifier("directWebClientBuilder") WebClient.Builder directWebClientBuilder
    ) {

        OllamaApi ollamaApi = OllamaApi.builder()
                .baseUrl(connectionProperties.getBaseUrl())
                .webClientBuilder(directWebClientBuilder)
                .build();

        OllamaOptions defaultOptions = OllamaOptions.builder()
                .model(chatProperties.getOptions().getModel())
                .temperature(chatProperties.getOptions().getTemperature())
                .build();

        return OllamaChatModel.builder()
                .ollamaApi(ollamaApi)
                .defaultOptions(defaultOptions)
                .build();
    }
}
