package com.biit.factmanager.persistence.entities;

import com.biit.eventstructure.event.IKafkaStorable;
import com.biit.factmanager.persistence.entities.values.FormrunnerValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.type.TypeReference;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity
@DiscriminatorValue("FormRunnerFact")
public class FormrunnerFact extends Fact<FormrunnerValue> implements IKafkaStorable {

    @Transient
    private FormrunnerValue formrunnerValue;


    @JsonCreator
    public FormrunnerFact() {
        super();
        formrunnerValue = new FormrunnerValue();
    }

    private FormrunnerValue getFormrunnerQuestionValue() {
        if (formrunnerValue == null) {
            formrunnerValue = getEntity();
        }
        return formrunnerValue;
    }

    @Override
    public void setEntity(FormrunnerValue entity) {
        formrunnerValue = entity;
        super.setEntity(entity);
    }

    @Override
    protected TypeReference<FormrunnerValue> getJsonParser() {
        return new TypeReference<FormrunnerValue>(){};
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
}
