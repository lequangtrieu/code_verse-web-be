package codeverse.com.web_be.service.EmailService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceSender {
    private final JavaMailSender emailSender;

    @Async
    public void sendResetPasswordEmail(String toEmail, String newPassword) throws MessagingException {
        String subject = "Your Password Has Been Reset";
        String htmlContent = """
        <div style="max-width: 600px; margin: auto; padding: 20px; font-family: Arial, sans-serif; border: 1px solid #ddd; border-radius: 10px;">
          <h2 style="color: #333; text-align: center;">Password Reset Successful</h2>
          <p style="font-size: 16px; color: #555;">Your new password is:</p>
          <div style="text-align: center; font-size: 18px; font-weight: bold; background: #f4f4f4; padding: 10px; border-radius: 5px; margin: 10px 0;">
            %s
          </div>
          <p style="font-size: 14px; color: #777;">Please log in and change your password for security reasons.</p>
          <hr style="border: none; border-top: 1px solid #ddd;">
          <p style="font-size: 12px; color: #aaa; text-align: center;">&copy; 2025 Our Service. All rights reserved.</p>
        </div>
    """.formatted(newPassword);

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(toEmail);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        emailSender.send(message);
    }

    @Async
    public void sendVerificationEmail(String email, String token) throws MessagingException {
        String subject = "Verify Your Email - Welcome to Our Service";
        String verificationLink = "http://localhost:8080/codeVerse/auth/verify-email/" + token;

        String htmlContent = """
        <div style="max-width: 600px; margin: auto; padding: 20px; font-family: Arial, sans-serif; border: 1px solid #ddd; border-radius: 10px;">
          <h2 style="color: #333; text-align: center;">Welcome to Our Service!</h2>
          <p style="font-size: 16px; color: #555;">Thank you for signing up. Please verify your email address by clicking the button below:</p>
          <div style="text-align: center; margin: 20px 0;">
            <a href="%s"
               style="background-color: #007bff; color: #fff; padding: 12px 20px; text-decoration: none; font-size: 16px; border-radius: 5px; display: inline-block;">
               Verify Your Email
            </a>
          </div>
          <p style="font-size: 14px; color: #777;">If you didn’t create an account, you can safely ignore this email.</p>
          <hr style="border: none; border-top: 1px solid #ddd;">
          <p style="font-size: 12px; color: #aaa; text-align: center;">&copy; 2025 Our Service. All rights reserved.</p>
        </div>
    """.formatted(verificationLink);

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(email);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        emailSender.send(message);
    }
}
