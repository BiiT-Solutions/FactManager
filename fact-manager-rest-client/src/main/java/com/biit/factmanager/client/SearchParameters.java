package com.biit.factmanager.client;

/*-
 * #%L
 * FactManager (Rest Client)
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

import java.util.Objects;

public enum SearchParameters {

    TENANT("tenant"),
    ORGANIZATION("organization"),
    UNIT("unit"),
    APPLICATION("application"),
    TAG("tag"),
    SUBJECT("subject"),
    SESSION("session"),
    GROUP("group"),
    ELEMENT("element"),
    ELEMENT_NAME("elementName"),
    FACT_TYPE("factType"),
    FROM("from"),
    TO("to"),
    LAST_DAYS("lastDays"),
    LATEST_BY_USER("latestByUser"),
    CREATED_BY("createdBy"),
    CREATED_AT("createdAt");

    private final String paramName;

    SearchParameters(String paramName) {
        this.paramName = paramName;
    }

    public String getParamName() {
        return paramName;
    }

    public static SearchParameters fromTag(String tag) {
        for (SearchParameters searchParameters : SearchParameters.values()) {
            if (Objects.equals(searchParameters.getParamName(), tag)) {
                return searchParameters;
            }
        }
        return null;
    }
}
