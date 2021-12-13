package com.biit.factmanager.persistence.entities;


import com.biit.factmanager.persistence.entities.values.FormRunnerValue;
import com.biit.factmanager.persistence.entities.values.StringValue;
import com.fasterxml.jackson.core.type.TypeReference;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;
import java.time.LocalDateTime;

/**
 * Tag is from Kafka
 * Group is ExaminationName
 * TenantId is Patient Id.
 * ElementId is Examination Id.
 * OrganizationId is Organization Id.
 */
@Entity
@DiscriminatorValue("FormRunnerFact")
public class FormRunnerFact extends Fact<FormRunnerValue> {

    @Transient
    private FormRunnerValue formRunnerValue;

    public FormRunnerFact() {
        super();
        formRunnerValue = new FormRunnerValue();
    }

    public FormRunnerFact(String elementId) {
        this();
        setElementId(elementId);
    }

    private FormRunnerValue getFormRunnerValue() {
        if (formRunnerValue == null) {
            formRunnerValue = getEntity();
        }
        return formRunnerValue;
    }

    @Override
    public void setEntity(FormRunnerValue entity) {
        formRunnerValue = entity;
        super.setEntity(entity);
    }

    @Override
    protected TypeReference<FormRunnerValue> getJsonParser() {
        return new TypeReference<FormRunnerValue>() {
        };
    }

    @Override
    public String getPivotViewerTag() {
        if (getFormRunnerValue() != null && getFormRunnerValue().getQuestion() != null) {
            return getFormRunnerValue().getQuestion();
        }
        return null;
    }

    @Override
    public String getPivotViewerValue() {
        if (getFormRunnerValue() != null && getFormRunnerValue().getScore() != null) {
            return getFormRunnerValue().getScore().toString();
        }
        return null;
    }

    @Override
    public String getPivotViewerItemName() {
        if (getFormRunnerValue() != null && getFormRunnerValue().getPatientName() != null) {
            return getFormRunnerValue().getPatientName();
        }
        return null;
    }

    @Override
    public Integer getPivotViewerItemImageIndex() {
        //Form scores that has the score, check by xpath.
        if (getFormRunnerValue() != null && (getFormRunnerValue().getXpath() == null || getFormRunnerValue().getXpath().length() < 2)
                && getFormRunnerValue().getScore() != null) {
            return getFormRunnerValue().getScore().intValue();
        }
        return null;
    }


}