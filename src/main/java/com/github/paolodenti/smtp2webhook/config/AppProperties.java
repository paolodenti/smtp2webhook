package com.github.paolodenti.smtp2webhook.config;

import java.net.URI;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Application properties.
 */
@Configuration
@ConfigurationProperties(prefix = "app")
@Data
public class AppProperties {

    private SmtpProperties smtp = new SmtpProperties();
    private WebhookProperties webhook = new WebhookProperties();

    @Data
    public static class SmtpProperties {

        private int port;
        private boolean details;
    }

    @Data
    public static class WebhookProperties {

        private URI url;
        private String contentType = "application/json";
        private String method = "POST";
    }
}
