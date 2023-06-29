package com.biit.factmanager.persistence.entities;

import com.biit.factmanager.persistence.entities.values.StringValue;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;


@Entity
@DiscriminatorValue("StringFact")
public class LogFact extends Fact<StringValue> {

    @Transient
    private StringValue stringValue;

    public LogFact() {
        super();
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
        if (stringValue == null) {
            setEntity(new StringValue(string));
        } else {
            stringValue.setString(string);
            setEntity(stringValue);
        }
    }

    @Override
    protected TypeReference<StringValue> getJsonParser() {
        return new TypeReference<>() {
        };
    }
}
