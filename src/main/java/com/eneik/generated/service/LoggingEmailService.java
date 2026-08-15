package com.eneik.generated.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class LoggingEmailService implements EmailService {
    private static final Logger log = LoggerFactory.getLogger(LoggingEmailService.class);
    private final List<String> sentEmails = Collections.synchronizedList(new ArrayList<>());

    @Override
    public void sendRecoveryEmail(String email, String token) {
        log.info("Sending recovery email to email={}", email);
        sentEmails.add(email + ":" + token);
    }

    public List<String> getSentEmails() {
        return new ArrayList<>(sentEmails);
    }

    public void clear() {
        sentEmails.clear();
    }
}
