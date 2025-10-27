package com.biit.factmanager.persistence.entities;

/*-
 * #%L
 * FactManager (Persistence)
 * %%
 * Copyright (C) 2020 - 2025 BiiT Sourcing Solutions S.L.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */


import com.biit.eventstructure.event.IKafkaStorable;
import com.biit.factmanager.logger.FactManagerLogger;
import com.biit.factmanager.persistence.entities.values.FormrunnerVariableValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;


/**
 * Tag is from Kafka
 * Group is ExaminationName
 * TenantId is Patient Id.
 * ElementId is Examination Id.
 * Organization is Organization.
 */
@Entity
@DiscriminatorValue("FormrunnerVariableFact")
public class FormrunnerVariableFact extends Fact<FormrunnerVariableValue> implements IKafkaStorable {

    @Transient
    private FormrunnerVariableValue formrunnerVariableValue;

    @JsonCreator
    public FormrunnerVariableFact() {
        super();
        formrunnerVariableValue = new FormrunnerVariableValue();
    }

    private FormrunnerVariableValue getFormrunnerVariableValue() {
        if (formrunnerVariableValue == null) {
            formrunnerVariableValue = getEntity();
        }
        return formrunnerVariableValue;
    }

    @Override
    public void setEntity(FormrunnerVariableValue entity) {
        formrunnerVariableValue = entity;
        super.setEntity(entity);
    }

    @Override
    protected TypeReference<FormrunnerVariableValue> getJsonParser() {
        return new TypeReference<>() {
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
            return getFormrunnerVariableValue().getValue();
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
                return (int) Double.parseDouble(getFormrunnerVariableValue().getValue());
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
