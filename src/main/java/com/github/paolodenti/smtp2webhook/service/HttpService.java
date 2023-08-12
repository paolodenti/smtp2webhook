package com.github.paolodenti.smtp2webhook.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.paolodenti.smtp2webhook.config.AppProperties;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class HttpService {

    private final AppProperties appProperties;
    private final ObjectMapper objectMapper;

    /**
     * Webhook post publisher.
     *
     * @param fields the fields to publish
     * @throws IOException          IOException
     * @throws InterruptedException InterruptedException
     */
    public void sendContent(Map<String, String> fields) throws IOException, InterruptedException, URISyntaxException {

        HttpRequest httpRequest;

        if ("POST".equalsIgnoreCase(appProperties.getWebhook().getMethod())) {
            log.debug("Posting webhook");

            httpRequest = HttpRequest.newBuilder()
                    .uri(appProperties.getWebhook().getUrl())
                    .header("Content-Type", appProperties.getWebhook().getContentType())
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(fields)))
                    .build();
        } else if ("GET".equalsIgnoreCase(appProperties.getWebhook().getMethod())) {
            log.debug("Getting webhook");

            URIBuilder uriBuilder = new URIBuilder(appProperties.getWebhook().getUrl().toString());
            for (Map.Entry<String, String> entry : fields.entrySet()) {
                uriBuilder.addParameter(entry.getKey(), entry.getValue());
            }

            httpRequest = HttpRequest.newBuilder()
                    .uri(uriBuilder.build())
                    .GET()
                    .build();
        } else {
            log.error("Unmanaged http method = {}", appProperties.getWebhook().getMethod());
            return;
        }

        HttpClient httpClient = HttpClient.newHttpClient();
        httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
    }
}
