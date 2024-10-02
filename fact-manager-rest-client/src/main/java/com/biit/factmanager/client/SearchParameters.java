package com.biit.factmanager.client;

import java.util.Objects;

public enum SearchParameters {

    TENANT("tenant"),
    ORGANIZATION("organization"),
    CUSTOMER("customer"),
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
