package com.capstone.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class MailServiceImpl implements MailService{
    private final JavaMailSender mailSender;
    public void sendEmail(String email, String title, String text) {
        SimpleMailMessage emailForm = createEmailForm(email, title, text);
        try {
            mailSender.send(emailForm);
        } catch (RuntimeException e) {
            throw new RuntimeException("email error", e);
        }
    }
    private SimpleMailMessage createEmailForm(String email, String title, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(title);
        message.setText(text);
        return message;
    }
}
