package com.biit.factmanager.client.exceptions;


import com.biit.factmanager.logger.ExceptionType;
import com.biit.factmanager.logger.LoggedException;
import org.springframework.http.HttpStatus;

public class InvalidResponseException extends LoggedException {
    private static final long serialVersionUID = -2510787123562846634L;

    public InvalidResponseException(Class<?> clazz, String message, ExceptionType type) {
        super(clazz, message, type, HttpStatus.NOT_FOUND);
    }

    public InvalidResponseException(Class<?> clazz, String message) {
        super(clazz, message, ExceptionType.WARNING, HttpStatus.NOT_FOUND);
    }

    public InvalidResponseException(Class<?> clazz) {
        this(clazz, "Comment not found");
    }

    public InvalidResponseException(Class<?> clazz, Throwable e) {
        super(clazz, e);
    }
}
