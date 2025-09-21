package com.email.ai.service.impl;

import com.email.ai.dto.Email;
import com.email.ai.dto.TriageResult;
import com.email.ai.service.AiService;
import com.email.ai.service.PreprocessService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Log4j2
public class PreprocessServiceImpl implements PreprocessService {
    private final AiService aiService;

    @Override
    public void processEmail(Email email) {
        log.info("Staring sanitize email Body");
        String body = sanitize(email.getBody());
        
        log.info("Mask PII Date from Subject");
        String subject = maskPII(email.getSubject());

        Map<String, String> ctx = Map.of("customerRisk", "LOW");

        TriageResult triage = aiService.triageEmail(subject, body, ctx);
        log.info("Email triage completed: {{ category: '{}', priority: '{}', confidence: {}, reason: '{}' }}",
                triage.getCategory(), triage.getPriority(), triage.getConfidence(), triage.getReason());


    }

    public String maskPII(String text) {
        if (text == null) return "";
        // mask card numbers
        text = text.replaceAll("\\b\\d{12,19}\\b", "[REDACTED_NUMBER]");
        // mask emails
        text = text.replaceAll("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}", "[REDACTED_EMAIL]");
        // mask account IDs (example pattern - update to your formats)
        text = text.replaceAll("\\bACC\\d{6,12}\\b", "[REDACTED_ACCOUNT]");
        return text;
    }

    public String sanitize(String htmlOrText) {
        return maskPII(toPlainText(htmlOrText));
    }

    public String toPlainText(String htmlOrText) {
        return Jsoup.parse(htmlOrText == null ? "" : htmlOrText).text();
    }
}
