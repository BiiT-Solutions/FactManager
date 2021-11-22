package com.biit.factmanager.persistence.entities;


import com.biit.factmanager.persistence.entities.values.FormRunnerValue;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;
import java.time.LocalDateTime;

/**
 * Tag is from Kafka
 * Category is ExaminationName
 * TenantId is Organization Id.
 * ElementId is Patient Id.
 */
@Entity
@DiscriminatorValue("FormRunnerFact")
public class FormRunnerFact extends Fact<FormRunnerValue> {

    @Transient
    private final FormRunnerValue formRunnerValue;

    public FormRunnerFact() {
        formRunnerValue = new FormRunnerValue();
    }

    public FormRunnerFact(String elementId) {
        formRunnerValue = new FormRunnerValue();
        setElementId(elementId);
    }

    @Override
    public void setCreatedAt(LocalDateTime localDateTime) {
        super.setCreatedAt(localDateTime);
    }


}