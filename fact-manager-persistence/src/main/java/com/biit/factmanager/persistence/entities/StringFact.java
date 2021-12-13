package com.biit.factmanager.persistence.entities;

import com.biit.factmanager.persistence.entities.values.StringValue;
import com.fasterxml.jackson.core.type.TypeReference;

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

    @Override
    public String getPivotViewerTag() {
        return "String";
    }

    @Override
    public String getPivotViewerValue() {
        return getString();
    }

    @Override
    public Integer getPivotViewerItemImageIndex() {
        return 1;
    }

    public String getString() {
        final StringValue stringValue = getEntity();
        if (stringValue != null) {
            return stringValue.getString();
        }
        return null;
    }

    public void setString(String string) {
        stringValue.setString(string);
        setEntity(stringValue);
    }

    @Override
    protected TypeReference<StringValue> getJsonParser() {
        return new TypeReference<StringValue>(){};
    }
}
