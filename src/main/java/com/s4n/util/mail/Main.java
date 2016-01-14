package com.s4n.util.mail;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Properties;
import java.util.Scanner;

import static javax.mail.Session.*;

public class Main {

    public static Scanner input = new Scanner(System.in);

    public static void main(String[] args) throws MessagingException {
        if (args.length < 3) {
            System.out.println("Please provide the minimum parameters (host, port, to)");
            return;
        }

        String host = args[0];
        String port = args[1];
        String to = args[2];
        String tls = args.length > 3 ? args[3] : "false";
        String from = args.length > 4 ? args[4] : null;

        String password = null;
        if (from != null) {
            System.out.println("Enter the password: ");
            password = input.next();
        }

        Session session = getDefaultInstance(buildProperties(host, port, tls, from), getAuthenticator(from, password));
        session.setDebug(true);

        Transport.send(buildMessage(to, from, session));
        System.out.println("SUCCESS");
    }

    private static MimeMessage buildMessage(String to, String from, Session session) throws MessagingException {
        MimeMessage message = new MimeMessage(session);
        if (from != null) {
            message.setFrom(new InternetAddress(from));
        }
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
        message.setSubject("Subject");

        String bodyFromTemplate = getBodyFromTemplate();
        message.setContent(bodyFromTemplate, "text/html");
        System.out.println(bodyFromTemplate);
        return message;
    }

    private static Properties buildProperties(String host, String port, String tls, String from) {
        Properties properties = new Properties();

        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.auth", from != null ? "true" : "false");
        properties.put("mail.smtp.starttls.enable", tls);


        if (Boolean.parseBoolean(tls)) {
            properties.put("mail.smtp.socketFactory.port", port);
            properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            properties.put("mail.smtp.socketFactory.fallback", "false");
        }
        return properties;
    }

    private static String getBodyFromTemplate() {
        Velocity.init();
        Template t = Velocity.getTemplate("./src/main/resources/test-mail.vm");

        VelocityContext ctx = new VelocityContext();

        Writer writer = new StringWriter();
        t.merge(ctx, writer);

        return writer.toString();
    }

    private static Authenticator getAuthenticator(final String from, final String password) {
        if (from == null) {
            return null;
        }
        return new Authenticator() {
            private PasswordAuthentication pa = new PasswordAuthentication(from, password);

            @Override
            public PasswordAuthentication getPasswordAuthentication() {
                return pa;
            }
        };
    }
}
