package codeverse.com.web_be.controller;

import codeverse.com.web_be.dto.request.AuthenRequest.*;
import codeverse.com.web_be.dto.response.AuthenResponse.AuthenticationResponse;
import codeverse.com.web_be.dto.response.AuthenResponse.IntrospectResponse;
import codeverse.com.web_be.dto.response.SystemResponse.ApiResponse;
import codeverse.com.web_be.dto.response.UserResponse.UserResponse;
import codeverse.com.web_be.entity.User;
import codeverse.com.web_be.enums.UserRole;
import codeverse.com.web_be.exception.AppException;
import codeverse.com.web_be.exception.ErrorCode;
import codeverse.com.web_be.repository.UserRepository;
import codeverse.com.web_be.service.AuthenService.AuthenticationService;
import codeverse.com.web_be.service.NotificationService.INotificationService;
import com.nimbusds.jose.JOSEException;
import jakarta.mail.MessagingException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;
    INotificationService notificationService;
    UserRepository userService;

    @GetMapping("/verify-email/{token}")
    public ResponseEntity<String> verifyEmailHtml(@PathVariable String token) {
        try {
            authenticationService.verifyEmail(token);
            String htmlSuccess = """
            <!DOCTYPE html>
            <html>
            <head><script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script></head>
            <body>
            <script>
                Swal.fire({
                    icon: 'success',
                    title: 'Email Verified!',
                    text: 'Your email has been successfully verified. You can now log in.',
                    confirmButtonText: 'Go to Login'
                }).then(() => {
                    window.location.href = 'https://code-verse-web-fe.vercel.app/';
                });
            </script>
            </body>
            </html>
        """;
            return ResponseEntity.ok().header("Content-Type", "text/html").body(htmlSuccess);
        } catch (AppException e) {
            String htmlError = """
            <!DOCTYPE html>
            <html>
            <head><script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script></head>
            <body>
            <script>
                Swal.fire({
                    icon: 'error',
                    title: 'Verification Failed',
                    text: 'The verification link is invalid or expired.',
                }).then(() => {
                    window.location.href = 'https://code-verse-web-fe.vercel.app/';
                });
            </script>
            </body>
            </html>
        """;
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .header("Content-Type", "text/html")
                    .body(htmlError);
        }
    }

    @GetMapping("/verify-email/{token}/instructor/{id}")
    public ResponseEntity<String> verifyEmailHtmlForInstructor(@PathVariable String token, @PathVariable Long id) {
        try {
            authenticationService.verifyEmail(token);
            List<User> admins = userService.findAll().stream()
                    .filter(u -> u.getRole().equals(UserRole.ADMIN))
                    .toList();
            User instructor = userService.findById(id)
                            .orElseThrow(() -> new ResourceNotFoundException("Instructor not found"));
            notificationService.notifyUsers(admins, instructor, "New Instructor Approval",
                    "<p>" + instructor.getName() + " has registered to become an instructor. " +
                            "<a href=\"https://code-verse-web-fe.vercel.app/admin-panel/approveInstructor" +
                            "\">View Detail >></a><p/>");
            String htmlSuccess = """
            <!DOCTYPE html>
            <html>
            <head><script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script></head>
            <body>
            <script>
                Swal.fire({
                    icon: 'success',
                    title: 'Email Verified!',
                    text: 'Your email has been successfully verified. Your registration will be sent for approval. Please wait for notification email.',
                }).then(() => {
                    window.location.href = 'https://code-verse-web-fe.vercel.app/';
                });
            </script>
            </body>
            </html>
        """;
            return ResponseEntity.ok().header("Content-Type", "text/html").body(htmlSuccess);
        } catch (AppException e) {
            String htmlError = """
            <!DOCTYPE html>
            <html>
            <head><script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script></head>
            <body>
            <script>
                Swal.fire({
                    icon: 'error',
                    title: 'Verification Failed',
                    text: 'The verification link is invalid or expired.',
                }).then(() => {
                    window.location.href = 'https://code-verse-web-fe.vercel.app/';
                });
            </script>
            </body>
            </html>
        """;
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .header("Content-Type", "text/html")
                    .body(htmlError);
        }
    }

    @PostMapping("/login")
    ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request){
        var result = authenticationService.authenticate(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/googleLogin")
    ApiResponse<AuthenticationResponse> authenticateGoogleLogin(@RequestBody AuthenticationRequest request){
        var result = authenticationService.authenticateGoogleLogin(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/signup")
    ApiResponse<AuthenticationResponse> authenticateSignup(@ModelAttribute SignUpRequest request) throws MessagingException {
        var result = authenticationService.authenticateSignup(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> refresh(@RequestHeader("Authorization") String authorizationHeader) throws ParseException, JOSEException {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        String refreshToken = authorizationHeader.substring(7);

        AuthenticationResponse response = authenticationService.refreshToken(refreshToken);

        return ResponseEntity.ok(
                ApiResponse.<AuthenticationResponse>builder()
                        .message("Token refreshed successfully")
                        .result(response)
                        .build()
        );
    }

    @PostMapping("/resetPassword")
    ResponseEntity<String> authenticateResetPassword(@RequestBody SignUpRequest request) throws MessagingException {
         authenticationService.authenticateResetPassword(request);
        return ResponseEntity.ok("A new password has been sent to your email.");
    }

    @PostMapping("/userDetail")
    ApiResponse<UserResponse> authenticateUserDetail(@RequestBody AuthenticationRequest request){
        var result = authenticationService.getUserByEmail(request.getUsername());
        return ApiResponse.<UserResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> authenticate(@RequestBody IntrospectRequest request)
            throws ParseException, JOSEException {
        var result = authenticationService.introspectToken(request);
        return ApiResponse.<IntrospectResponse>builder().result(result).build();
    }

    @PostMapping("/logout")
    ApiResponse<Void> logout(@RequestBody LogoutRequest request) throws ParseException, JOSEException {
        authenticationService.logout(request);
        return ApiResponse.<Void>builder().build();
    }

    @PostMapping("/changePassword")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> authenticateChangePassword(
            @RequestBody ChangePasswordRequest request) {
        AuthenticationResponse response = authenticationService.authenticateChangePassword(request);

        return ResponseEntity.ok(
                ApiResponse.<AuthenticationResponse>builder()
                        .result(response)
                        .build()
        );
    }
}
