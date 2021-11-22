package com.biit.factmanager.persistence.entities;


import com.biit.factmanager.persistence.entities.values.FormRunnerValue;

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
    public void setCreatedAt(LocalDateTime localDateTime) {
        super.setCreatedAt(localDateTime);
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
    public String getPivotViewerValueItemName() {
        if (getFormRunnerValue() != null && getFormRunnerValue().getPatientName() != null) {
            return getFormRunnerValue().getPatientName();
        }
        return null;
    }

    @Override
    public Integer getPivotViewerImageIndex() {
        if (getFormRunnerValue() != null && getFormRunnerValue().getScore() != null) {
            return getFormRunnerValue().getScore().intValue();
        }
        return null;
    }


}