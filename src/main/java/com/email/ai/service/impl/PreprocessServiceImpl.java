package com.email.ai.service.impl;

import com.email.ai.dto.Email;
import com.email.ai.service.PreprocessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class PreprocessServiceImpl implements PreprocessService {

    @Override
    public void processEmail(Email email) {

    }
}
