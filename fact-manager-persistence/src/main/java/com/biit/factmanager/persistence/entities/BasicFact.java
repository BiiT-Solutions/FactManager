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
