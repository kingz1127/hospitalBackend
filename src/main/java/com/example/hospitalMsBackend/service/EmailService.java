package com.example.hospitalMsBackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    // This reads the 'from' email we just moved in the yml
    @Value("${spring.mail.from}")
    private String senderEmail;

    public void sendResetPasswordEmail(String to, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(senderEmail); // <--- THIS IS CRITICAL
            message.setTo(to);
            message.setSubject("Password Reset Request");
            message.setText("To reset your password, click the link below:\n" +
                    "http://localhost:3000/reset-password?token=" + token);
            mailSender.send(message);
            log.info("Reset email sent to {}", to);
        } catch (Exception e) {
            log.error("Failed to send reset email: {}", e.getMessage());
        }
    }

    public void sendOtpEmail(String email, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(senderEmail); // <--- THIS IS CRITICAL
            message.setTo(email);
            message.setSubject("Your Patient Portal Access Code");
            message.setText("Your one-time access code is: " + otp + "\nExpires in 10 mins.");
            mailSender.send(message);
            log.info("OTP sent to {}", email);
        } catch (Exception e) {
            log.error("Failed to send OTP: {}", e.getMessage());
        }
    }

    public void sendOnboardingEmail(String to, String username, String tempPassword) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(senderEmail); // <--- THIS IS CRITICAL
            message.setTo(to);
            message.setSubject("Welcome to HospitalMS - Your Staff Account");
            message.setText("Hello,\n\nYour account has been created.\n" +
                    "Username: " + username + "\n" +
                    "Temporary Password: " + tempPassword + "\n\n" +
                    "Please login and reset your password.");
            mailSender.send(message);
            log.info("Onboarding email sent to {}", to);
        } catch (Exception e) {
            log.error("Failed to send onboarding email: {}", e.getMessage());
        }
    }
}