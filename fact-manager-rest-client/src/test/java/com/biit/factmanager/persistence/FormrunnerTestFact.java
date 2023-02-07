package com.biit.factmanager.persistence;


import com.biit.eventstructure.event.IKafkaStorable;
import com.biit.factmanager.client.IFact;
import com.biit.factmanager.logger.FactManagerLogger;
import com.biit.factmanager.persistence.entities.Fact;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.type.TypeReference;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity
@DiscriminatorValue("FormrunnerTestFact")
public class FormrunnerTestFact extends Fact<FormrunnerTestValue> implements IFact {

    @Transient
    private FormrunnerTestValue formrunnerVariableValue;

    @JsonCreator
    public FormrunnerTestFact() {
        super();
        formrunnerVariableValue = new FormrunnerTestValue();
    }

    private FormrunnerTestValue getFormrunnerVariableValue() {
        if (formrunnerVariableValue == null) {
            formrunnerVariableValue = getEntity();
        }
        return formrunnerVariableValue;
    }

    @Override
    public void setEntity(FormrunnerTestValue entity) {
        formrunnerVariableValue = entity;
        super.setEntity(entity);
    }

    @Override
    protected TypeReference<FormrunnerTestValue> getJsonParser() {
        return new TypeReference<FormrunnerTestValue>() {
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