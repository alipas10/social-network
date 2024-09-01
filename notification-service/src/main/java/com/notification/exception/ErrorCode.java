package com.notification.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1201, "Uncategorized error", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1206, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1207, "You do not have permission", HttpStatus.FORBIDDEN),
    CANNOT_SEND_EMAIL(1208, "Cannot send email", HttpStatus.BAD_REQUEST),
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
