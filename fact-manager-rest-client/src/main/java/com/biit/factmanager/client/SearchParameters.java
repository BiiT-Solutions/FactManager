package com.biit.factmanager.client;

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
    FACT_TYPE("factType"),
    FROM("from"),
    TO("to"),
    LAST_DAYS("lastDays");

    private final String paramName;

    SearchParameters(String paramName) {
        this.paramName = paramName;
    }

    public String getParamName() {
        return paramName;
    }
}
