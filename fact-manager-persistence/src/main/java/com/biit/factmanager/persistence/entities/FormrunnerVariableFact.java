package com.biit.factmanager.persistence.entities;


import com.biit.eventstructure.event.IKafkaStorable;
import com.biit.factmanager.logger.FactManagerLogger;
import com.biit.factmanager.persistence.entities.values.FormrunnerVariableValue;
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
@DiscriminatorValue("FormrunnerVariableFact")
public class FormrunnerVariableFact<T> extends Fact<FormrunnerVariableValue<T>> implements IKafkaStorable {

    @Transient
    private FormrunnerVariableValue<T> formrunnerVariableValue;

    @JsonCreator
    public FormrunnerVariableFact() {
        super();
        formrunnerVariableValue = new FormrunnerVariableValue<T>();
    }

    private FormrunnerVariableValue<T> getFormrunnerVariableValue() {
        if (formrunnerVariableValue == null) {
            formrunnerVariableValue = getEntity();
        }
        return formrunnerVariableValue;
    }

    @Override
    public void setEntity(FormrunnerVariableValue<T> entity) {
        formrunnerVariableValue = entity;
        super.setEntity(entity);
    }

    @Override
    protected TypeReference<FormrunnerVariableValue<T>> getJsonParser() {
        return new TypeReference<FormrunnerVariableValue<T>>() {
        };
    }

    @Override
    public String getPivotViewerTag() {
        if (getFormrunnerVariableValue() != null && getFormrunnerVariableValue().getXpath() != null) {
            return getFormrunnerVariableValue().getFormElement();
        }
        return null;
    }

    @Override
    public String getPivotViewerValue() {
        if (getFormrunnerVariableValue() != null && getFormrunnerVariableValue().getValue() != null) {
            return getFormrunnerVariableValue().getValue().toString();
        }
        return null;
    }

    @Override
    public String getPivotViewerItemName() {
        if (getFormrunnerVariableValue() != null && getFormrunnerVariableValue().getItemName() != null) {
            return getFormrunnerVariableValue().getItemName();
        }
        return null;
    }

    @Override
    public Integer getPivotViewerItemImageIndex() {
        //Form scores that has the score, check by xpath.
        if (getFormrunnerVariableValue() != null && getFormrunnerVariableValue().getXpath() != null
                && getFormrunnerVariableValue().getValue() != null) {
            try {
                return (int) Double.parseDouble(getFormrunnerVariableValue().getValue().toString());
            } catch (NumberFormatException e) {
                FactManagerLogger.warning(this.getClass().getName(), "Not a numerical value '"
                        + getFormrunnerVariableValue().getValue()
                        + "' on '"
                        + getFormrunnerVariableValue().getXpath() + "'.");
            }
        }
        return null;
    }
}