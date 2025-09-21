package com.email.ai.service.impl;

import com.email.ai.dto.TriageResult;
import com.email.ai.service.AiService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Log4j2
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {
    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    @Override
    public TriageResult triageEmail(String subject, String body, Map<String, String> ctx) {
        log.info("Build prompt to call chat Client");
        String prompt = buildPrompt(subject, body, ctx);

        log.info("calling client for email triage");
        String response = chatClient.prompt(prompt).call().content();
        log.info("Raw LLM response: {}", response);

        try {
            JsonNode node = objectMapper.readTree(response);
            String priority = node.path("priority").asText("MEDIUM");
            double confidence = node.path("confidence").asDouble(0.0);
            String category = node.path("category").asText("");
            String reason = node.path("reason").asText("");
            return new TriageResult(priority, confidence, category, reason);
        } catch (Exception e) {
            // Fallback: if not valid JSON, return safe default and log for analysis
            // Log the response for debugging
            // logger.warn("LLM parse failed; rawResponse={}", response);
            return new TriageResult("MEDIUM", 0.0, "parsing_error", "LLM output parse failed");
        }
    }

    private String buildPrompt(String subject, String body, Map<String,String> ctx) {
        // include a few-shot and strict JSON schema instruction
        return """
            You are an email triage assistant for a fintech. Return ONLY valid JSON matching the schema:
            { "priority":"CRITICAL|HIGH|MEDIUM|LOW", "confidence":0.0-1.0, "category":"...", "reason":"brief explanation (1-2 sentences)" }
            Context: %s

            Example1:
            Subject: "Unauthorized debit of ₹50,000"
            Body: "I see an unauthorized debit of ₹50,000 from my account. Please reverse it immediately."
            Output: {"priority":"CRITICAL","confidence":0.98,"category":"fraud","reason":"mentions unauthorized high-value debit"}

            Example2:
            Subject: "How to update my address?"
            Body: "I want to change my registered address."
            Output: {"priority":"LOW","confidence":0.99,"category":"KYC","reason":"simple account update request"}

            Now classify the following:
            Subject: %s
            Body: %s
            """.formatted(ctx==null ? "{}" : ctx.toString(), subject, body);
    }
}
