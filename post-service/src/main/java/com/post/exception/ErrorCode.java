package com.post.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1201, "Uncategorized error", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1206, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1207, "You do not have permission", HttpStatus.FORBIDDEN),
    CANNOT_SAVE_POST(1304, "Cannot save post", HttpStatus.BAD_REQUEST),
    CANNOT_SAVE_GET_ALL_POST(1305, "Cannot save post", HttpStatus.BAD_REQUEST),

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
