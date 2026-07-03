package com.atendeai.modules.chatbot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

/**
 * Serviço de integração com a Claude API (Anthropic).
 * Responsável por montar a requisição e processar a resposta do LLM.
 */
@Slf4j
@Service
public class ClaudeApiService {

    @Autowired
    @Qualifier("claudeWebClient")
    private WebClient claudeWebClient;

    @Value("${app.claude.model}")
    private String model;

    @Value("${app.claude.max-tokens}")
    private int maxTokens;

    @Value("${app.claude.system-prompt}")
    private String systemPrompt;

    /**
     * Envia mensagens para o Claude com tools opcionais e retorna a resposta.
     *
     * @param messages      Lista de mensagens no formato {role, content}
     * @param tools         Tools disponíveis para o Claude chamar (pode ser null)
     * @param knowledgeCtx  Contexto adicional da base de conhecimento
     * @return Resposta bruta da API do Claude
     */
    public Map<String, Object> sendMessage(
            List<Map<String, Object>> messages,
            List<Map<String, Object>> tools,
            String knowledgeCtx) {

        String systemWithCtx = buildSystemPrompt(knowledgeCtx);

        Map<String, Object> body = new java.util.HashMap<>(Map.of(
                "model", model,
                "max_tokens", maxTokens,
                "system", systemWithCtx,
                "messages", messages
        ));

        if (tools != null && !tools.isEmpty()) {
            body.put("tools", tools);
        }

        log.debug("Chamando Claude API com {} mensagens", messages.size());

        return claudeWebClient.post()
                .uri("/v1/messages")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .doOnError(e -> log.error("Erro ao chamar Claude API", e))
                .block();
    }

    private String buildSystemPrompt(String knowledgeCtx) {
        if (knowledgeCtx == null || knowledgeCtx.isBlank()) {
            return systemPrompt;
        }
        return systemPrompt + "\n\n## Base de Conhecimento do Cliente\n" + knowledgeCtx;
    }

    /**
     * Tools disponíveis para o chatbot chamar.
     * O Claude decide quando e como chamá-las baseado na conversa.
     */
    public List<Map<String, Object>> getAvailableTools() {
        return List.of(
                Map.of(
                        "name", "check_availability",
                        "description", "Verifica a disponibilidade de horários para agendamento",
                        "input_schema", Map.of(
                                "type", "object",
                                "properties", Map.of(
                                        "date", Map.of("type", "string", "description", "Data no formato YYYY-MM-DD"),
                                        "service_type", Map.of("type", "string", "description", "Tipo de serviço desejado")
                                ),
                                "required", List.of("date")
                        )
                ),
                Map.of(
                        "name", "book_appointment",
                        "description", "Agenda um atendimento para o cliente",
                        "input_schema", Map.of(
                                "type", "object",
                                "properties", Map.of(
                                        "service_type", Map.of("type", "string"),
                                        "title", Map.of("type", "string"),
                                        "scheduled_at", Map.of("type", "string", "description", "ISO 8601 datetime"),
                                        "duration_minutes", Map.of("type", "integer"),
                                        "notes", Map.of("type", "string")
                                ),
                                "required", List.of("service_type", "title", "scheduled_at")
                        )
                ),
                Map.of(
                        "name", "get_client_history",
                        "description", "Busca o histórico de serviços do cliente",
                        "input_schema", Map.of(
                                "type", "object",
                                "properties", Map.of(
                                        "limit", Map.of("type", "integer", "description", "Quantidade de registros (padrão: 5)")
                                )
                        )
                )
        );
    }
}
