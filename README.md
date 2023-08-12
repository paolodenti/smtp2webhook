# SMTP To webhook

Simple SMTP to to webhook, for internal use.

It sends the content of an incoming email to a webhook.

SMTP protocol is implemented without TLS/SSL/Authentication.
Only for internal use. **Do not publicly expose it**.

## How to use it

```bash
# send with POST, as json
WEBHOOK_URL=<your webhook> \
  ./mvnw spring-boot:run
```

```bash
# send with GET and query params
WEBHOOK_URL=<your webhook> \
WEBHOOK_METHOD=GET \
  ./mvnw spring-boot:run
```

```bash
# force a content type
WEBHOOK_URL=<your webhook> \
WEBHOOK_METHOD=POST \
WEBHOOK_CONTENT_TYPE=text/plain \
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

### Message format

#### POST

When `WEBHOOK_METHOD` is `POST` (default value), the webhook receives a json payload, in post, like the one below.

The content type is set to the value of `WEBHOOK_CONTENT_TYPE` (default `application/json`).

```json
{
  "recipient": "therecipient@example.com",
  "from": "thesender@example.com",
  "content": "this is the email body\r\non two lines",
  "subject": "this is the subject"
}
```

#### GET

When `WEBHOOK_METHOD` is `GET`, the webhook is invoked with the following URL

```
<your webhook>?recipient=therecipient@example.com&from=thesender@example.com&content=the-content&subject=the-subject"
```

### Docker image

```
docker run --rm -d \
  -e WEBHOOK_URL=<your webhook> \
  -p 25:2525 \
  paolodenti/smtp2webhook:latest
```
