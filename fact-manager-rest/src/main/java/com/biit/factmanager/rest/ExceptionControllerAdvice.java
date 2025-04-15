package com.biit.factmanager.rest;

import com.biit.factmanager.core.providers.exceptions.FactNotFoundException;
import com.biit.factmanager.core.providers.exceptions.InvalidParameterException;
import com.biit.factmanager.logger.FactManagerLogger;
import com.biit.server.exceptions.ErrorResponse;
import com.biit.server.exceptions.ServerExceptionControllerAdvice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

//@ControllerAdvice
public class ExceptionControllerAdvice extends ServerExceptionControllerAdvice {

    @ExceptionHandler(FactNotFoundException.class)
    public ResponseEntity<Object> factNotFoundException(IllegalArgumentException ex) {
        FactManagerLogger.errorMessage(this.getClass(), ex);
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage(), "fact_not_found", ex), HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(InvalidParameterException.class)
    public ResponseEntity<Object> customInvalidParameterException(IllegalArgumentException ex) {
        FactManagerLogger.errorMessage(this.getClass(), ex);
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage(), "invalid_parameter", ex), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> runtimeException(Exception ex) {
        FactManagerLogger.errorMessage(this.getClass().getName(), ex);
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage(), ex), HttpStatus.BAD_REQUEST);
    }
}
