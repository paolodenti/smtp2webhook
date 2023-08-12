# SMTP To webhook

Simple SMTP to to webhook, for internal use.

It posts the content of an incoming email to a webhook.

SMTP protocol is implemented without TLS/SSL/Authentication.
Only for internal use. **Do not publicly expose it**.

## How to use it

```
WEBHOOK_URL=<your webhook> \
  ./mvnw spring-boot:run
```

### Listening port

By default, the smtp listens on port 2525.

To change the listening port set the `SMTP_PORT` environment variable, e.g.

```
WEBHOOK_URL=<your webhook> \
SMTP_PORT=25 \
  ./mvnw spring-boot:run
```

### Message content

The webhook receives a json payload, in post, like the one below

```json
{
  "recipient": "therecipient@example.com",
  "from": "thesender@example.com",
  "content": "this is the email body\r\non two lines",
  "subject": "this is the subject"
}
```
### Docker image

```
docker run --rm -d \
  -e WEBHOOK_URL=<your webhook> \
  -p 25:2525
  paolodenti/smtp2webhook:latest
```
