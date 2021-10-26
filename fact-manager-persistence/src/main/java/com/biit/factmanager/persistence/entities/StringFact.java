package com.biit.factmanager.persistence.entities;

import javax.persistence.Entity;

@Entity
public class StringFact extends Fact<StringValue> {
    private final StringValue value;

    public StringFact() {
        this.value = new StringValue();
    }

    public String getString() {
        return value.getString();
    }

    //public void setString(String string) { this.string = string; }
}

class StringValue {
    private String string;

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }
}
