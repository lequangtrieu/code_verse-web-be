package codeverse.com.web_be.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Uncategorized error", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1002, "User existed", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1003, "Username must be at least 3 characters", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1004, "Password must be at least 8 characters", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1005, "User not existed", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),
    NOT_EXISTED_ENTITY(1008, "Entity not existed", HttpStatus.BAD_REQUEST),
    EMAIL_ALREADY_VERIFIED(1009, "EMAIL ALREADY VERIFIED", HttpStatus.BAD_REQUEST),
    USER_BANNED(1010, "Your account has been banned", HttpStatus.FORBIDDEN),
    UN_VERIFY_EMAIL(1011, "Your account need to verify to login", HttpStatus.UNAUTHORIZED),
    RESOURCE_NOT_EXISTED(1012, "Resource not found", HttpStatus.NOT_FOUND),
    ILLEGAL_ARGS(1013, "Invalid arguments", HttpStatus.BAD_REQUEST),
    INVALID_TOKEN(1014, "Invalid Google ID token", HttpStatus.UNAUTHORIZED),
    EMAIL_REGISTERED_WITH_PASSWORD(1015, "This email is already registered with a password. Please login with your password.", HttpStatus.BAD_REQUEST),
    RESET_PASSWORD_NOT_SUPPORTED_FOR_GOOGLE(1016, "Password reset is not supported for Google accounts.", HttpStatus.BAD_REQUEST),
    CHANGE_PASSWORD_NOT_SUPPORTED_FOR_GOOGLE(1017, "Password change is not supported for Google accounts.", HttpStatus.BAD_REQUEST),
    OLD_PASSWORD_INCORRECT(1018, "Old password is incorrect.", HttpStatus.BAD_REQUEST),
    PASSWORD_SAME_AS_OLD(1019, "New password must be different from the current password.", HttpStatus.BAD_REQUEST),
    FREE_COURSE_IN_CART(1020, "Your cart contains a free course, which does not require payment.", HttpStatus.BAD_REQUEST),
    ;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private int code;
    private String message;
    private HttpStatusCode statusCode;
}
