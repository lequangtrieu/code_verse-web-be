package codeverse.com.web_be.service.EmailService;

import codeverse.com.web_be.entity.Course;
import codeverse.com.web_be.entity.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;

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

    @Async
    public void sendFreeCourseConfirmationEmail(User user, Course course) throws MessagingException {
        String subject = "🎉 You’ve Successfully Enrolled in a Free Course!";
        String htmlContent = String.format("""
        <div style="max-width: 600px; margin: auto; padding: 20px; font-family: Arial, sans-serif; border: 1px solid #ddd; border-radius: 10px;">
            <h2 style="color: #1677ff; text-align: center;">Hello %s</h2>
            <p style="font-size: 16px; color: #333;">Congratulations! You’ve successfully enrolled in the free course:</p>
            <h3 style="color: #007bff; text-align: center;">%s</h3>
            <p style="font-size: 16px; color: #555;">We’re excited to have you start learning with us. This is your chance to explore, grow, and become better at your craft.</p>
            <p style="font-size: 16px; color: #555;">To access the course, simply login to your account and navigate to "My Courses".</p>
            <div style="text-align: center; margin: 20px 0;">
                <a href="http://localhost:3000/"
                   style="background-color: #4da6ff; color: white; padding: 12px 20px; text-decoration: none; font-size: 16px; border-radius: 5px;">
                   Go to My Courses
                </a>
            </div>
            <p style="font-size: 14px; color: #777;">If you have any questions or feedback, feel free to reach out to our support team.</p>
            <hr style="border: none; border-top: 1px solid #ddd;">
            <p style="font-size: 12px; color: #aaa; text-align: center;">&copy; 2025 CodeVerse. Happy learning!</p>
        </div>
    """, user.getUsername(), course.getTitle());

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
        helper.setTo(user.getUsername());
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        emailSender.send(message);
    }

    @Async
    public void sendPaidCoursesConfirmationEmail(User user, List<Course> courses) throws MessagingException {
        String subject = "🎉 Your Purchase Was Successful - Welcome to New Courses!";

        StringBuilder courseListHtml = new StringBuilder();
        for (Course course : courses) {
            courseListHtml.append(String.format(
                    "<li style='margin-bottom: 8px; font-size: 16px; color: #007bff;'>%s</li>",
                    course.getTitle()
            ));
        }

        String htmlContent = String.format("""
        <div style="max-width: 600px; margin: auto; padding: 20px; font-family: Arial, sans-serif; border: 1px solid #ddd; border-radius: 10px;">
            <h2 style="color: #2e6da4; text-align: center;">Hello %s,</h2>
            <p style="font-size: 16px; color: #333;">Thank you for your purchase! You’ve successfully enrolled in the following course(s):</p>
            <ul style="padding-left: 20px;">%s</ul>
            <p style="font-size: 16px; color: #555;">We hope you enjoy your learning journey. To start learning, just log in and go to "My Courses".</p>
            <div style="text-align: center; margin: 20px 0;">
                <a href="http://localhost:3000/"
                   style="background-color: #4da6ff; color: white; padding: 12px 20px; text-decoration: none; font-size: 16px; border-radius: 5px;">
                   Go to My Courses
                </a>
            </div>
            <p style="font-size: 14px; color: #777;">If you have any questions, feel free to reach out to our support team anytime.</p>
            <hr style="border: none; border-top: 1px solid #ddd;">
            <p style="font-size: 12px; color: #aaa; text-align: center;">&copy; 2025 CodeVerse. Happy learning!</p>
        </div>
    """, user.getName(), courseListHtml.toString());

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
        helper.setTo(user.getUsername());
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        emailSender.send(message);
    }
}
