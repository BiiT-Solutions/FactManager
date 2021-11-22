package com.biit.factmanager.persistence.entities.exceptions;

public class FactValueInvalidException extends RuntimeException {
    public FactValueInvalidException(Throwable e){
        super(e);
    }
}
