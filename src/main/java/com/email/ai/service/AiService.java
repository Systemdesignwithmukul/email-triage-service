package com.email.ai.service;

import com.email.ai.dto.TriageResult;

import java.util.Map;

public interface AiService {
    TriageResult triageEmail(String subject, String body, Map<String, String> ctx);
}
