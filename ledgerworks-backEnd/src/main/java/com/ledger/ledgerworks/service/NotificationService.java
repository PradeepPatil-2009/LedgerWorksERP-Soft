package com.ledger.ledgerworks.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Sends operational notifications to the admin.
 *
 * The {@link JavaMailSender} is injected OPTIONALLY through an
 * {@link ObjectProvider} so the application boots with ZERO mail
 * configuration. Mail is only sent when email notifications are enabled
 * AND a mail sender is actually available; otherwise the notification is
 * logged. This method never throws.
 */
@Service
public class NotificationService {

    private static final Logger log =
            LoggerFactory.getLogger(NotificationService.class);

    private final ObjectProvider<JavaMailSender> mailSenderProvider;

    @Value("${app.notifications.email-enabled:false}")
    private boolean emailEnabled;

    @Value("${app.notifications.admin-email:admin@example.com}")
    private String adminEmail;

    public NotificationService(
            ObjectProvider<JavaMailSender> mailSenderProvider
    ) {
        this.mailSenderProvider = mailSenderProvider;
    }

    /**
     * Notify the admin. Sends an email when enabled and possible,
     * otherwise logs. Never throws.
     */
    public void notify(String subject, String body) {

        JavaMailSender mailSender =
                mailSenderProvider.getIfAvailable();

        if (!emailEnabled || mailSender == null) {

            log.info(
                    "[NOTIFICATION] to={} | {} | {}",
                    adminEmail,
                    subject,
                    body
            );
            return;
        }

        try {

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(adminEmail);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);

            log.info(
                    "Notification email sent to {}: {}",
                    adminEmail,
                    subject
            );

        } catch (Exception e) {

            // Never let a notification failure bubble up.
            log.warn(
                    "Failed to send notification email to {}: {}",
                    adminEmail,
                    e.getMessage()
            );
        }
    }
}
