package com.biit.factmanager.persistence.entities.exceptions;

public class ValueAlreadySet extends RuntimeException {

    public ValueAlreadySet() {
        super();
    }

    public ValueAlreadySet(Throwable e) {
        super(e);
    }
}
