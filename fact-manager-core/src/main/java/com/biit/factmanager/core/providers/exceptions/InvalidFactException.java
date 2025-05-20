package com.biit.factmanager.core.providers.exceptions;

import com.biit.factmanager.logger.ExceptionType;
import com.biit.factmanager.logger.LoggedException;
import org.springframework.http.HttpStatus;

import java.io.Serial;

public class InvalidFactException extends LoggedException {

    @Serial
    private static final long serialVersionUID = -8218023565259890374L;

    public InvalidFactException(Class<?> clazz, String message, ExceptionType type) {
        super(clazz, message, type, HttpStatus.NOT_FOUND);
    }

    public InvalidFactException(Class<?> clazz, String message) {
        super(clazz, message, ExceptionType.WARNING, HttpStatus.NOT_FOUND);
    }

    public InvalidFactException(Class<?> clazz) {
        this(clazz, "Fact content is incorrect");
    }

    public InvalidFactException(Class<?> clazz, Throwable e) {
        super(clazz, e);
    }

    public InvalidFactException(Class<?> clazz, String message, Throwable e) {
        super(clazz, message, e);
    }
}
