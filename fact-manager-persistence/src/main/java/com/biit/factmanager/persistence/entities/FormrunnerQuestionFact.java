package com.biit.factmanager.persistence.entities;


import com.biit.eventstructure.event.IKafkaStorable;
import com.biit.factmanager.persistence.entities.values.FormrunnerQuestionValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.type.TypeReference;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

/**
 * Tag is from Kafka
 * Group is ExaminationName
 * TenantId is Patient Id.
 * ElementId is Examination Id.
 * OrganizationId is Organization Id.
 */
@Entity
@DiscriminatorValue("FormRunnerQuestionFact")
public class FormrunnerQuestionFact extends Fact<FormrunnerQuestionValue> implements IKafkaStorable {

    @Transient
    private FormrunnerQuestionValue formrunnerQuestionValue;

    @JsonCreator
    public FormrunnerQuestionFact() {
        super();
        formrunnerQuestionValue = new FormrunnerQuestionValue();
    }

    private FormrunnerQuestionValue getFormrunnerQuestionValue() {
        if (formrunnerQuestionValue == null) {
            formrunnerQuestionValue = getEntity();
        }
        return formrunnerQuestionValue;
    }

    @Override
    public void setEntity(FormrunnerQuestionValue entity) {
        formrunnerQuestionValue = entity;
        super.setEntity(entity);
    }

    @Override
    protected TypeReference<FormrunnerQuestionValue> getJsonParser() {
        return new TypeReference<FormrunnerQuestionValue>() {
        };
    }

    @Override
    public String getPivotViewerTag() {
        if (getFormrunnerQuestionValue() != null && getFormrunnerQuestionValue().getQuestion() != null) {
            return getFormrunnerQuestionValue().getQuestion();
        }
        return null;
    }

    @Override
    public String getPivotViewerValue() {
        return null;
    }

    @Override
    public String getPivotViewerItemName() {
        if (getFormrunnerQuestionValue() != null && getFormrunnerQuestionValue().getItemName() != null) {
            return getFormrunnerQuestionValue().getItemName();
        }
        return null;
    }

    @Override
    public Integer getPivotViewerItemImageIndex() {
        return null;
    }
}