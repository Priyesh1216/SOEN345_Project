package com.example.soen345_project.domain.services;

import android.os.StrictMode;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
public class NotifService {

    private static final String SENDER_EMAIL = "soen345.project@gmail.com";
    private static final String SENDER_PASSWORD = "khgx olxr mxjn rclg";
    public NotifService() {}

    public void sendConfirmationMsg(String recipient, String message) {
        sendEmail(recipient, "Reservation Confirmed", message);
    }

    public void sendCancellationMsg(String recipient, String message) {
        sendEmail(recipient, "Reservation Cancelled", message);
    }

    private void sendEmail(String recipientEmail, String subject, String body) {
        new Thread(() -> {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);

            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

            Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
                }
            });

            try {
                MimeMessage mimeMessage = new MimeMessage(session);
                mimeMessage.setFrom(new InternetAddress(SENDER_EMAIL));
                mimeMessage.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse(recipientEmail));
                mimeMessage.setSubject(subject);
                mimeMessage.setText(body);
                Transport.send(mimeMessage);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }).start();
    }
}