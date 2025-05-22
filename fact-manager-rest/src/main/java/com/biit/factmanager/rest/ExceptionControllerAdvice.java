package com.biit.factmanager.rest;

import com.biit.drools.form.xls.exceptions.InvalidXlsElementException;
import com.biit.factmanager.core.providers.exceptions.FactNotFoundException;
import com.biit.factmanager.core.providers.exceptions.InvalidFactException;
import com.biit.factmanager.core.providers.exceptions.InvalidParameterException;
import com.biit.factmanager.logger.FactManagerLogger;
import com.biit.kafka.exceptions.InvalidEventException;
import com.biit.server.exceptions.ErrorResponse;
import com.biit.server.exceptions.ServerExceptionControllerAdvice;
import com.biit.usermanager.client.exceptions.ElementNotFoundException;
import com.biit.usermanager.client.exceptions.InvalidConfigurationException;
import com.biit.usermanager.client.exceptions.InvalidValueException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
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

    @ExceptionHandler(InvalidEventException.class)
    public ResponseEntity<Object> invalidEventException(Exception ex) {
        FactManagerLogger.errorMessage(this.getClass().getName(), ex);
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage(), "cannot_connect_to_kafka", ex), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ElementNotFoundException.class)
    public ResponseEntity<Object> elementNotFoundException(Exception ex) {
        FactManagerLogger.errorMessage(this.getClass().getName(), ex);
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage(), "not_found", ex), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidConfigurationException.class)
    public ResponseEntity<Object> invalidConfigurationException(Exception ex) {
        FactManagerLogger.errorMessage(this.getClass().getName(), ex);
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage(), "invalid_configuration_exception", ex), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InvalidValueException.class)
    public ResponseEntity<Object> invalidValueException(Exception ex) {
        FactManagerLogger.errorMessage(this.getClass().getName(), ex);
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage(), "invalid_parameter", ex), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidFactException.class)
    public ResponseEntity<Object> invalidFactException(Exception ex) {
        FactManagerLogger.errorMessage(this.getClass().getName(), ex);
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage(), "invalid_fact", ex), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidXlsElementException.class)
    public ResponseEntity<Object> invalidXlsElementException(Exception ex) {
        FactManagerLogger.errorMessage(this.getClass().getName(), ex);
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage(), "invalid_xls", ex), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
