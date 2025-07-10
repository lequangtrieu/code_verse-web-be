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
    COURSE_NOT_FREE(1021, "This course is not free and cannot be added via free method.", HttpStatus.BAD_REQUEST),
    COURSE_ALREADY_OWNED(1022, "You already own this course.", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED_ACCESS(1023, "You are not allowed to access these cart items.", HttpStatus.FORBIDDEN),
    INSTRUCTOR_NOT_ACTIVE(102, "INSTRUCTOR IS NOT ACTIVE BY ADMIN.", HttpStatus.BAD_REQUEST),
    WITHDRAWAL_REQUEST_ALREADY_EXISTS(2001, "You already have a pending or unverified withdrawal request.", HttpStatus.BAD_REQUEST),
    WITHDRAWAL_AMOUNT_TOO_LOW(2002, "Withdrawal amount must be at least 50,000 VND.", HttpStatus.BAD_REQUEST),
    WITHDRAWAL_INVALID_STATUS(2003, "Request is not in a valid status for this operation.", HttpStatus.BAD_REQUEST),
    WITHDRAWAL_NOT_FOUND(2004, "Withdrawal request not found.", HttpStatus.NOT_FOUND),
    WITHDRAWAL_UNAUTHORIZED_CANCEL(2005, "You are not authorized to cancel this withdrawal request.", HttpStatus.FORBIDDEN),
    WITHDRAWAL_INVALID_TOKEN(2006, "Invalid or expired withdrawal verification token.", HttpStatus.BAD_REQUEST),
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
