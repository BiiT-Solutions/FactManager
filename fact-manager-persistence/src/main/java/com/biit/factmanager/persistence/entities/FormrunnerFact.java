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
import com.biit.factmanager.persistence.entities.values.FormrunnerValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;


@Entity
@DiscriminatorValue("FormrunnerFact")
public class FormrunnerFact extends Fact<FormrunnerValue> implements IKafkaStorable {

    @Transient
    private FormrunnerValue formrunnerValue;

    @JsonCreator
    public FormrunnerFact() {
        super();
    }

    private FormrunnerValue getFormrunnerValue() {
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
        return new TypeReference<FormrunnerValue>() {
        };
    }

    @Override
    public String getPivotViewerTag() {
        if (getFormrunnerValue() != null && getFormrunnerValue().getFormName() != null) {
            return getFormrunnerValue().getFormName();
        }
        return null;
    }

    @Override
    public String getPivotViewerValue() {
        if (getFormrunnerValue() != null && getFormrunnerValue().getFormName() != null) {
            return getFormrunnerValue().getFormJson();
        }
        return null;
    }

    @Override
    public String getPivotViewerItemName() {
        if (getFormrunnerValue() != null && getFormrunnerValue().getFormName() != null) {
            return getFormrunnerValue().getPatientName();
        }
        return null;
    }

    @Override
    public Integer getPivotViewerItemImageIndex() {
        if (getFormrunnerValue() != null && getFormrunnerValue().getFormName() != null) {
            return Integer.parseInt(getFormrunnerValue().getVersion());
        }
        return null;
    }
}
