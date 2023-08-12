package com.github.paolodenti.smtp2webhook.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.paolodenti.smtp2webhook.config.AppProperties;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Properties;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.stereotype.Service;
import org.subethamail.smtp.helper.SimpleMessageListener;

@Service
@Slf4j
@RequiredArgsConstructor
public class SmtpListener implements SimpleMessageListener {

    private final AppProperties appProperties;
    private final ObjectMapper objectMapper;

    @Override
    public boolean accept(String from, String recipient) {

        return true;
    }

    @Override
    public void deliver(String from, String recipient, InputStream data) {

        log.debug("from={}, recipient={}", from, recipient);
        try {
            MimeMessage mimeMessage = convertToMimeMessage(data);

            Map<String, String> fields = Map.of(
                    "from", from,
                    "recipient", recipient,
                    "subject", mimeMessage.getSubject(),
                    "content", mimeMessage.getContent().toString()
            );

            postContent(fields);
        } catch (MessagingException | IOException | InterruptedException | URISyntaxException e) {
            log.error("Error while converting to MimeMessage, dropping the message", e);
        }
    }

    /**
     * Converter from smtp inputStream to a mime message.
     *
     * @param data smtp inputStream
     * @return mimeMessage
     * @throws MessagingException MessagingException
     */
    private MimeMessage convertToMimeMessage(InputStream data) throws MessagingException {

        Session session = Session.getDefaultInstance(new Properties());
        try {
            return new MimeMessage(session, data);
        } catch (MessagingException e) {
            throw new MessagingException();
        }
    }

    /**
     * Webhook post publisher.
     *
     * @param fields the fields to publish
     * @throws IOException          IOException
     * @throws InterruptedException InterruptedException
     */
    private void postContent(Map<String, String> fields) throws IOException, InterruptedException, URISyntaxException {

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
