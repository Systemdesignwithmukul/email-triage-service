package com.email.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor  // Default constructor
@AllArgsConstructor // Constructor with all fields
public class TriageResult {
    private String priority; // CRITICAL|HIGH|MEDIUM|LOW
    private double confidence;
    private String category;
    private String reason;

    // Optional: Add a custom toString for better logging
    @Override
    public String toString() {
        return String.format("TriageResult{category='%s', priority='%s', confidence=%.3f, reason='%s'}",
                category, priority, confidence, reason);
    }
}