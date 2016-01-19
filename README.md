# test-mail-client-velocity
Java client for test email sending with Velocity templates.

## Run
```
java -jar target/MailClient-1.0-SNAPSHOT.jar MAIL_SERVER MAIL_SERVER_PORT TARGET_MAIL [TLS] [SENDER_MAIL]
```

Example:
```
java -jar target/MailClient-1.0-SNAPSHOT.jar smtp.gmail.com 465 jhovanyamaya@seven4n.com true pruebas4n@gmail.com
```