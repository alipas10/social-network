package com.profile.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1101, "Uncategorized error", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1102, "User existed", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1103, "Username must be at least {min} characters", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1104, "Password must be at least {min} characters", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1105, "User not existed", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1106, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1107, "You do not have permission", HttpStatus.FORBIDDEN),
    INVALID_DOB(1108, "Your age must be at least {min}", HttpStatus.BAD_REQUEST),
    USER_INVITED(1109, "You have been send invite for this user", HttpStatus.BAD_REQUEST),

    ;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}
