package com.biit.factmanager.persistence.entities;

import com.biit.factmanager.persistence.entities.values.StringValue;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity
@DiscriminatorValue("StringFact")
public class StringFact extends Fact<StringValue> {
    @Transient
    private final StringValue stringValue;

    public StringFact() {
        this.stringValue = new StringValue();
    }

    public String getString() {
        return stringValue.getString();
    }

    public void setString(String string) { stringValue.setString(string); }
}
