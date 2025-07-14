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

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

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

    @Async
    public void sendWithdrawalVerificationEmail(String toEmail, String token, BigDecimal amount, Long instructorId) throws MessagingException {
        String subject = "Verify Your Instructor Withdrawal Request";

        NumberFormat currencyFormat = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        String formattedAmount = currencyFormat.format(amount) + " vnd?";

        String verificationLink = String.format(
                "http://localhost:8080/codeVerse/api/instructors/%d/withdrawals/verify?token=%s",
                instructorId, token
        );

        String htmlContent = """
        <div style="max-width: 600px; margin: auto; padding: 20px; font-family: Arial, sans-serif; border: 1px solid #ddd; border-radius: 10px;">
            <h2 style="color: #333; text-align: center;">Withdrawal Verification</h2>
            <p style="font-size: 16px; color: #555;">You recently requested a withdrawal with the following amount:</p>
            <div style="text-align: center; font-size: 18px; font-weight: bold; background: #f4f4f4; padding: 10px; border-radius: 5px; margin: 10px 0;">
                %s
            </div>
            <p style="font-size: 16px; color: #555;">To complete this request, please verify by clicking the button below:</p>
            <div style="text-align: center; margin: 20px 0;">
                <a href="%s"
                   style="background-color: #28a745; color: #fff; padding: 12px 24px; text-decoration: none; font-size: 16px; border-radius: 5px; display: inline-block;">
                   Verify Withdrawal Request
                </a>
            </div>
            <p style="font-size: 14px; color: #777;">If you did not make this request, please disregard this email.</p>
            <hr style="border: none; border-top: 1px solid #ddd;">
            <p style="font-size: 12px; color: #aaa; text-align: center;">&copy; 2025 CodeVerse. All rights reserved.</p>
        </div>
    """.formatted(formattedAmount, verificationLink);

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(toEmail);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        emailSender.send(message);
    }


    @Async
    public void sendCourseCompletionEmail(User user, Course course) throws MessagingException {
        String subject = "🏆 Congratulations on Completing Your Course!";

        String htmlContent = String.format("""
    <div style="max-width: 600px; margin: auto; padding: 24px; font-family: Arial, sans-serif; border: 1px solid #e2e2e2; border-radius: 12px; background: #fdfdfd;">
        <div style="text-align: center; margin-bottom: 20px;">
            <img src="https://cdn-icons-png.flaticon.com/512/2278/2278992.png" alt="Trophy Icon" style="width: 80px; height: 80px;" />
        </div>
        <h2 style="text-align: center; color: #1677ff;">Congratulations, %s! 🎉</h2>
        <p style="font-size: 16px; color: #444; text-align: center;">
            You’ve successfully completed the course:
        </p>
        <h3 style="color: #333; text-align: center; font-size: 20px;">%s</h3>
        <p style="font-size: 16px; color: #555; margin-top: 20px;">
            We’re proud of your dedication and effort. Completing a course is a big achievement — keep pushing your learning journey forward!
        </p>
        <div style="text-align: center; margin: 30px 0;">
            <a href="http://localhost:3000/"
               style="background-color: #28a745; color: white; padding: 12px 24px; text-decoration: none; font-size: 16px; border-radius: 6px;">
               Continue Learning
            </a>
        </div>
        <p style="font-size: 14px; color: #888; text-align: center;">
            Need help or want to share your feedback? We’re just an email away.
        </p>
        <hr style="border: none; border-top: 1px solid #ddd; margin-top: 30px;">
        <p style="font-size: 12px; color: #aaa; text-align: center;">&copy; 2025 CodeVerse. Keep growing 🌱</p>
    </div>
    """, user.getName(), course.getTitle());

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
        helper.setTo(user.getUsername()); // username là email
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        emailSender.send(message);
    }
}
