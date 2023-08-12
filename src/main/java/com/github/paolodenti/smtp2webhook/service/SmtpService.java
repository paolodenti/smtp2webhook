package com.github.paolodenti.smtp2webhook.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.subethamail.smtp.server.SMTPServer;

@Service
@Slf4j
@RequiredArgsConstructor
public class SmtpService {

    private final SMTPServer smtpServer;

    /**
     * SMTP service bootstrapper.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void startListener() {

        log.info("Starting SMTP listener");

        smtpServer.start();
    }
}
