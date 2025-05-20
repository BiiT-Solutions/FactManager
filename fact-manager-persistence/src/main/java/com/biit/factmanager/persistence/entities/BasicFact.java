package com.biit.factmanager.persistence.entities;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;


@Entity
@DiscriminatorValue("BasicFact")
public class BasicFact extends Fact<String> {

    public BasicFact() {
        super();
    }

    @Override
    public String getPivotViewerTag() {
        return null;
    }

    @Override
    public String getPivotViewerValue() {
        return null;
    }

    @Override
    public Integer getPivotViewerItemImageIndex() {
        return null;
    }

    @Override
    protected TypeReference<String> getJsonParser() {
        return new TypeReference<String>() {
        };
    }

    @Override
    @JsonSetter
    //@JsonDeserialize(using = JsonValueDeserializer.class)
    @JsonSerialize(using = JsonValueSerializer.class)
    public void setValue(String value) {
        super.setValue(value);
    }

    @Override
    @JsonGetter
    //@JsonSerialize(using = JsonValueSerializer.class)
    @JsonDeserialize(using = JsonValueDeserializer.class)
    public String getValue() {
        return super.getValue();
    }
}
