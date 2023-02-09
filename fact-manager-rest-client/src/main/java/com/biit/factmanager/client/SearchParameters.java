package com.biit.factmanager.client;

public enum SearchParameters {

    TENANT("tenant"),
    ORGANIZATION("organization"),
    CUSTOMER("customer"),
    APPLICATION("application"),
    TAG("tag"),
    GROUP("group"),
    ELEMENT("element"),
    PROCESS("process"),
    FROM("from"),
    TO("to"),
    LAST_DAYS("lastDays");

    private final String paramName;

    private SearchParameters(String paramName) {
        this.paramName = paramName;
    }

    public String getParamName() {
        return paramName;
    }
}
