package com.biit.factmanager.persistence.entities.values;

import com.biit.kafka.events.EventPayload;

public class StringValue implements EventPayload {
    private String string;

    public StringValue() {

    }

    public StringValue(String value) {
        setString(value);
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    @Override
    public String toString() {
        return "StringValue{"
                + "string='" + string + '\''
                + '}';
    }
}
