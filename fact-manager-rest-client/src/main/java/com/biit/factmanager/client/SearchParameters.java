package com.biit.factmanager.client;

public enum SearchParameters {

    TENANT_ID("tenantId"),
    ORGANIZATION_ID("organizationId"),
    TAG("tag"),
    GROUP("group"),
    ELEMENT_ID("elementId"),
    PROCESS_ID("processId"),
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
