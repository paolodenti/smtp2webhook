package com.github.paolodenti.smtp2webhook.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Properties;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.subethamail.smtp.helper.SimpleMessageListener;

@Service
@Slf4j
@RequiredArgsConstructor
public class SmtpListener implements SimpleMessageListener {

    private final HttpService httpService;

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

            httpService.sendContent(fields);
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
}
