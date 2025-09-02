package codeverse.com.web_be.service.EmailService;

import codeverse.com.web_be.entity.Course;
import codeverse.com.web_be.entity.User;
import codeverse.com.web_be.entity.WithdrawalRequest;
import codeverse.com.web_be.enums.UserRole;
import codeverse.com.web_be.service.FunctionHelper.FunctionHelper;
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
    private final FunctionHelper functionHelper;

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
        User user = functionHelper.getActiveUserByUsername(email);
        String verificationLink = "https://codeverse-backend-431045531117.asia-southeast1.run.app/codeVerse/auth/verify-email/" + token + (user.getRole().equals(UserRole.INSTRUCTOR) ? "/instructor/" + user.getId() : "");

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
    public void sendImportedUserWelcomeEmail(User user) throws MessagingException {
        String subject = "🎉 Welcome to CodeVerse! Your Account Is Ready";

        String htmlContent = String.format("""
                <div style="max-width: 600px; margin: auto; padding: 24px; font-family: Arial, sans-serif; border: 1px solid #e2e2e2; border-radius: 12px; background: #fdfdfd;">
                    <h2 style="text-align: center; color: #1677ff;">Welcome to CodeVerse, %s!</h2>
                    <p style="font-size: 16px; color: #444; text-align: center;">
                        We’re excited to have you onboard. An account has been created for you on CodeVerse.
                    </p>
                    <p style="font-size: 16px; color: #555; text-align: center;">
                        You can now log in securely using your Google account.
                    </p>
                    <div style="text-align: center; margin: 30px 0;">
                        <a href="https://code-verse-web-fe.vercel.app/"
                           style="background-color: #4285F4; color: white; padding: 12px 24px; text-decoration: none; font-size: 16px; border-radius: 6px;">
                           Go to our Homepage
                        </a>
                    </div>
                    <p style="font-size: 14px; color: #888; text-align: center;">
                        If you have any questions, feel free to <a href="mailto:dolvapple@gmail.com">contact our support team</a>.
                    </p>
                    <hr style="border: none; border-top: 1px solid #ddd; margin-top: 30px;">
                    <p style="font-size: 12px; color: #aaa; text-align: center;">&copy; 2025 CodeVerse. Learn. Grow. Succeed.</p>
                </div>
                """, user.getName());

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(
                message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name()
        );

        helper.setTo(user.getUsername());
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        emailSender.send(message);
    }

    @Async
    public void sendInstructorApprovalEmail(User user) throws MessagingException {
        String subject = "🎓 Congratulations! You're Now an Instructor on CodeVerse";

        String htmlContent = String.format("""
                <div style="max-width: 600px; margin: auto; padding: 24px; font-family: Arial, sans-serif; border: 1px solid #e2e2e2; border-radius: 12px; background: #f0f8ff;">
                    <h2 style="text-align: center; color: #1677ff;">Welcome to the Instructor Community, %s!</h2>
                    <p style="font-size: 16px; color: #444; text-align: center;">
                        Your request to become an instructor on <strong>CodeVerse</strong> has been approved.
                    </p>
                    <p style="font-size: 16px; color: #555; text-align: center;">
                        You now have access to create and manage your own courses. We’re so excited!
                    </p>
                    <div style="text-align: center; margin: 30px 0;">
                        <a href="https://code-verse-web-fe.vercel.app/"
                           style="background-color: #28a745; color: white; padding: 12px 24px; text-decoration: none; font-size: 16px; border-radius: 6px;">
                           Start with us
                        </a>
                    </div>
                    <p style="font-size: 14px; color: #888; text-align: center;">
                        Have questions? <a href="mailto:dolvapple@gmail.com">Contact our support team</a>.
                    </p>
                    <hr style="border: none; border-top: 1px solid #ddd; margin-top: 30px;">
                    <p style="font-size: 12px; color: #aaa; text-align: center;">&copy; 2025 CodeVerse. Inspire and educate.</p>
                </div>
                """, user.getName());

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(
                message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name()
        );

        helper.setTo(user.getUsername());
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        emailSender.send(message);
    }

    @Async
    public void sendInstructorRejectionEmail(User user) throws MessagingException {
        String subject = "⚠️ Instructor Registration Request Denied";

        String htmlContent = String.format("""
                <div style="max-width: 600px; margin: auto; padding: 24px; font-family: Arial, sans-serif; border: 1px solid #e2e2e2; border-radius: 12px; background: #fff8f8;">
                    <h2 style="text-align: center; color: #d32f2f;">Instructor Registration Unsuccessful</h2>
                    <p style="font-size: 16px; color: #444; text-align: center;">
                        Dear %s,
                    </p>
                    <p style="font-size: 16px; color: #555;">
                        Thank you for your interest in becoming an instructor on <strong>CodeVerse</strong>. Unfortunately, your request has not been approved at this time.
                    </p>
                    <p style="font-size: 16px; color: #555;">
                        You’re welcome to continue using our platform as a learner, and you may apply again in the future if your circumstances change.
                    </p>
                    <p style="font-size: 14px; color: #888; text-align: center;">
                        Have questions? <a href="mailto:dolvapple@gmail.com">Reach out to our team</a>.
                    </p>
                    <hr style="border: none; border-top: 1px solid #ddd; margin-top: 30px;">
                    <p style="font-size: 12px; color: #aaa; text-align: center;">&copy; 2025 CodeVerse. Keep learning 🌱</p>
                </div>
                """, user.getName());

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(
                message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name()
        );

        helper.setTo(user.getUsername());
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        emailSender.send(message);
    }

    @Async
    public void sendCourseApprovalEmail(User instructor, Course course) throws MessagingException {
        String subject = "✅ Your Course Has Been Approved on CodeVerse!";

        String htmlContent = String.format("""
    <div style="max-width: 600px; margin: auto; padding: 24px; font-family: Arial, sans-serif; border: 1px solid #e2e2e2; border-radius: 12px; background: #f9fffa;">
        <h2 style="text-align: center; color: #28a745;">Congratulations, %s! 🎉</h2>
        <p style="font-size: 16px; color: #444; text-align: center;">
            Your course <strong>"%s"</strong> has been <span style="color:#28a745; font-weight:bold;">approved</span> and is now live on <strong>CodeVerse</strong>.
        </p>
        <p style="font-size: 16px; color: #555; text-align: center;">
            Students can now explore your course and start learning from your expertise.
        </p>
        <div style="text-align: center; margin: 30px 0;">
            <a href="https://code-verse-web-fe.vercel.app/course/%d"
               style="background-color: #1677ff; color: white; padding: 12px 24px; text-decoration: none; font-size: 16px; border-radius: 6px;">
               View Your Course
            </a>
        </div>
        <p style="font-size: 14px; color: #888; text-align: center;">
            Have questions? <a href="mailto:dolvapple@gmail.com">Contact our support team</a>.
        </p>
        <hr style="border: none; border-top: 1px solid #ddd; margin-top: 30px;">
        <p style="font-size: 12px; color: #aaa; text-align: center;">&copy; 2025 CodeVerse. Keep inspiring learners worldwide.</p>
    </div>
    """, instructor.getName(), course.getTitle(), course.getId());

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(
                message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name()
        );

        helper.setTo(instructor.getUsername());
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        emailSender.send(message);
    }

    @Async
    public void sendCourseRejectionEmail(User instructor, Course course) throws MessagingException {
        String subject = "❌ Your Course Submission Was Not Approved on CodeVerse";

        String htmlContent = String.format("""
    <div style="max-width: 600px; margin: auto; padding: 24px; font-family: Arial, sans-serif; border: 1px solid #e2e2e2; border-radius: 12px; background: #fff5f5;">
        <h2 style="text-align: center; color: #d9534f;">Hello, %s</h2>
        <p style="font-size: 16px; color: #444; text-align: center;">
            Unfortunately, your course <strong>"%s"</strong> has been <span style="color:#d9534f; font-weight:bold;">rejected</span>.
        </p>
        <p style="font-size: 16px; color: #555; text-align: center;">
            You can update your course and resubmit it for approval at any time.
        </p>
        <div style="text-align: center; margin: 30px 0;">
            <a href="https://code-verse-web-fe.vercel.app/instructor-panel/courses/%d"
               style="background-color: #1677ff; color: white; padding: 12px 24px; text-decoration: none; font-size: 16px; border-radius: 6px;">
               Update and Resubmit
            </a>
        </div>
        <p style="font-size: 14px; color: #888; text-align: center;">
            Need help? <a href="mailto:dolvapple@gmail.com">Contact our support team</a>.
        </p>
        <hr style="border: none; border-top: 1px solid #ddd; margin-top: 30px;">
        <p style="font-size: 12px; color: #aaa; text-align: center;">&copy; 2025 CodeVerse. Keep improving and keep inspiring.</p>
    </div>
    """, instructor.getName(), course.getTitle(), course.getId());

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(
                message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name()
        );

        helper.setTo(instructor.getUsername());
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
                            <a href="https://code-verse-web-fe.vercel.app/course"
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
                            <a href="https://code-verse-web-fe.vercel.app/course"
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
                "https://codeverse-backend-431045531117.asia-southeast1.run.app/codeVerse/api/instructors/%d/withdrawals/verify?token=%s",
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
    public void sendWithdrawalConfirmationEmail(WithdrawalRequest request) throws MessagingException {
        String subject = "💰 Withdrawal Request Approved — Please Confirm";

        String confirmationLink = "https://codeverse-backend-431045531117.asia-southeast1.run.app/codeVerse/api/instructors/" + request.getInstructor().getId() + "/withdrawals/" + request.getId() + "/confirm";

        String htmlContent = String.format("""
                <div style="max-width: 600px; margin: auto; padding: 24px; font-family: Arial, sans-serif; border: 1px solid #e2e2e2; border-radius: 12px; background: #fdfdfd;">
                    <h2 style="text-align: center; color: #1677ff;">Withdrawal Approved</h2>
                    <p style="font-size: 16px; color: #444; text-align: center;">
                        Dear %s,
                    </p>
                    <p style="font-size: 16px; color: #555;">
                        Your withdrawal request has been approved.
                    </p>
                    <p style="font-size: 16px; color: #555;">
                        To proceed, please confirm the withdrawal by clicking the button below:
                    </p>
                    <div style="text-align: center; margin: 30px 0;">
                        <a href="%s"
                           style="background-color: #28a745; color: white; padding: 12px 24px; text-decoration: none; font-size: 16px; border-radius: 6px;">
                           Confirm Withdrawal
                        </a>
                    </div>
                    <p style="font-size: 14px; color: #888; text-align: center;">
                        The Admin Team would be informed about your confirmation.
                    </p>
                    <hr style="border: none; border-top: 1px solid #ddd; margin-top: 30px;">
                    <p style="font-size: 12px; color: #aaa; text-align: center;">&copy; 2025 CodeVerse. Secure and trusted.</p>
                </div>
                """, request.getInstructor().getName(), confirmationLink);

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(
                message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name()
        );

        helper.setTo(request.getInstructor().getUsername());
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        emailSender.send(message);
    }

    @Async
    public void sendCourseCompletionEmail(User user, Course course) throws MessagingException {
        String subject = "🏆 Congratulations on Completing Your Course!";

        String certificateUrl = String.format(
                "https://code-verse-web-fe.vercel.app/certificate/%d?userId=%d",
                course.getId(),
                user.getId()
        );

        String htmlContent = String.format("""
                <div style="max-width: 600px; margin: auto; padding: 24px; font-family: Arial, sans-serif; border: 1px solid #e2e2e2; border-radius: 12px; background: #fdfdfd;">
                    <h2 style="text-align: center; color: #1677ff;">Congratulations, %s! 🎉</h2>
                    <p style="font-size: 16px; color: #444; text-align: center;">
                        You’ve successfully completed the course:
                    </p>
                    <h3 style="color: #333; text-align: center; font-size: 20px;">%s</h3>
                    <p style="font-size: 16px; color: #555; margin-top: 20px;">
                        We’re proud of your dedication and effort. Completing a course is a big achievement — keep pushing your learning journey forward!
                    </p>
                    <div style="text-align: center; margin: 30px 0;">
                        <a href="%s"
                           style="background-color: #1677ff; color: white; padding: 12px 24px; text-decoration: none; font-size: 16px; border-radius: 6px;">
                           View Your Certificate
                        </a>
                    </div>
                    <p style="font-size: 14px; color: #888; text-align: center;">
                        Need help or want to share your feedback? We’re just an email away.
                    </p>
                    <hr style="border: none; border-top: 1px solid #ddd; margin-top: 30px;">
                    <p style="font-size: 12px; color: #aaa; text-align: center;">&copy; 2025 CodeVerse. Keep growing 🌱</p>
                </div>
                """, user.getName(), course.getTitle(), certificateUrl);

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(
                message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
        helper.setTo(user.getUsername()); // username là email
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        emailSender.send(message);
    }

    @Async
    public void sendUserBannedEmail(User user) throws MessagingException {
        String subject = "🚫 Account Banned Due to Multiple Reports";

        String htmlContent = String.format("""
                <div style="max-width: 600px; margin: auto; padding: 24px; font-family: Arial, sans-serif; border: 1px solid #e2e2e2; border-radius: 12px; background: #fff8f8;">
                    <h2 style="text-align: center; color: #d32f2f;">Your Account Has Been Banned</h2>
                    <p style="font-size: 16px; color: #444; text-align: center;">
                        Dear %s,
                    </p>
                    <p style="font-size: 16px; color: #555; margin-top: 10px;">
                        We regret to inform you that your account has been banned due to receiving multiple reports regarding violations of our community guidelines.
                    </p>
                    <p style="font-size: 16px; color: #555;">
                        This action was taken after careful consideration to ensure the safety and integrity of our platform.
                    </p>
                    <p style="font-size: 16px; color: #555; margin-top: 20px;">
                        If you believe this was a mistake or would like to appeal the ban, you may reply directly to this email with an explanation or request for review.
                    </p>
                    <hr style="border: none; border-top: 1px solid #ddd; margin-top: 30px;">
                    <p style="font-size: 12px; color: #aaa; text-align: center;">&copy; 2025 CodeVerse. Your digital space for learning.</p>
                </div>
                """, user.getName());

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(
                message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name()
        );

        helper.setTo(user.getUsername());
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        emailSender.send(message);
    }

    @Async
    public void sendUserUnbannedEmail(User user) throws MessagingException {
        String subject = "✅ Your Account Has Been Reinstated";

        String htmlContent = String.format("""
                <div style="max-width: 600px; margin: auto; padding: 24px; font-family: Arial, sans-serif; border: 1px solid #e2e2e2; border-radius: 12px; background: #f7fff7;">
                    <h2 style="text-align: center; color: #388e3c;">Welcome Back, %s! 🎉</h2>
                    <p style="font-size: 16px; color: #444; text-align: center;">
                        Your account has been successfully reinstated.
                    </p>
                    <p style="font-size: 16px; color: #555; margin-top: 20px;">
                        After a thorough review, we’ve lifted the ban on your account. You now have full access to all platform features again.
                    </p>
                    <p style="font-size: 16px; color: #555;">
                        We appreciate your patience and encourage you to continue following our community guidelines to ensure a positive experience for all users.
                    </p>
                    <div style="text-align: center; margin: 30px 0;">
                        <a href="https://code-verse-web-fe.vercel.app"
                           style="background-color: #388e3c; color: white; padding: 12px 24px; text-decoration: none; font-size: 16px; border-radius: 6px;">
                           Go to Dashboard
                        </a>
                    </div>
                    <p style="font-size: 14px; color: #888; text-align: center;">
                        Have questions? Just reply to this email and we’ll be happy to help.
                    </p>
                    <hr style="border: none; border-top: 1px solid #ddd; margin-top: 30px;">
                    <p style="font-size: 12px; color: #aaa; text-align: center;">&copy; 2025 CodeVerse. Welcome back 🌱</p>
                </div>
                """, user.getName());

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(
                message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name()
        );

        helper.setTo(user.getUsername());
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        emailSender.send(message);
    }

}
