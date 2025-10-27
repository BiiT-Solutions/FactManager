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

import com.biit.factmanager.persistence.entities.values.StringValue;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;


@Entity
@DiscriminatorValue("LogFact")
public class LogFact extends Fact<StringValue> {

    @Transient
    private StringValue stringValue;

    public LogFact() {
        super();
    }

    @Override
    public String getPivotViewerTag() {
        return "String";
    }

    @Override
    public String getPivotViewerValue() {
        return getString();
    }

    @Override
    public Integer getPivotViewerItemImageIndex() {
        return 1;
    }

    public String getString() {
        final StringValue stringValue = getEntity();
        if (stringValue != null) {
            return stringValue.getString();
        }
        return null;
    }

    public void setString(String string) {
        if (stringValue == null) {
            setEntity(new StringValue(string));
        } else {
            stringValue.setString(string);
            setEntity(stringValue);
        }
    }

    @Override
    protected TypeReference<StringValue> getJsonParser() {
        return new TypeReference<>() {
        };
    }
}
