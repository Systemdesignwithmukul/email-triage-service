package com.email.ai.controller;

import com.email.ai.api.EmailApi;
import com.email.ai.dto.Email;
import com.email.ai.service.PreprocessService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class EmailController implements EmailApi {
    private final PreprocessService preprocessService;

    @Override
    public ResponseEntity<Void> ingest(Email email) {
        preprocessService.processEmail(email);
        return ResponseEntity.ok().build();
    }
}
