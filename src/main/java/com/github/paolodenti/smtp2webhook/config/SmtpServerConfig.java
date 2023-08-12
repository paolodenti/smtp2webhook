package com.github.paolodenti.smtp2webhook.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.subethamail.smtp.helper.SimpleMessageListener;
import org.subethamail.smtp.server.SMTPServer;

@Configuration
@RequiredArgsConstructor
public class SmtpServerConfig {

    private final AppProperties appProperties;
    private final SimpleMessageListener simpleMessageListener;

    /**
     * SMTPServer bean.
     *
     * @return an SMTPServer instance
     */
    @Bean
    public SMTPServer smtpServer() {

        return SMTPServer
                .port(appProperties.getSmtp().getPort())
                .simpleMessageListener(simpleMessageListener)
                .build();
    }
}
