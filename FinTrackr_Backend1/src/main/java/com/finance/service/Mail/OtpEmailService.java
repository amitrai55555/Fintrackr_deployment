package com.finance.service.Mail;

import com.finance.entity.User;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Sends OTP emails for bank account verification.
 */
@Service
public class OtpEmailService {

    private final JavaMailSender mailSender;
    private final String testRecipient;
    private final String fromAddress;

    public OtpEmailService(JavaMailSender mailSender, Environment environment) {
        this.mailSender = mailSender;
        this.testRecipient = environment.getProperty("app.mail.test-recipient", "").trim();
        this.fromAddress = environment.getProperty("spring.mail.username", "FinTrackrTech@gmail.com");
    }

    public void sendOtpVerificationEmail(User user, String otp) {
        String recipient = (!testRecipient.isEmpty()) ? testRecipient : user.getEmail();

        String subject = "Bank Account Verification OTP";
        String text = """
                Hi %s,

                Your One-Time Password (OTP) for verifying your FinTrackr account is:

                %s

                This OTP is valid for the next 10 minutes. Please do not share it with anyone.

                If you did not request this, please ignore this email.

                Stay secure,
                Team FinTrackr
                """.formatted(user.getFirstName(), otp);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(recipient);
        message.setSubject(subject);
        message.setText(text);

        mailSender.send(message);
    }
}
