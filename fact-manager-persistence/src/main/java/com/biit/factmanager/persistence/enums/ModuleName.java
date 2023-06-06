package com.biit.factmanager.persistence.enums;

public enum ModuleName {
    ACTIVITY("activity"), BACKLOGS("backlogs"), COSTS_MODULE("costs_module"), CALENDAR("calendar"),
    REPORTING_MODULE("reporting_module"), DOCUMENTS("documents"), BOARDS("boards"), MEETINGS("meetings"),
    NEWS("news"), REPOSITORY("repository"), TIME_TRACKING("time_tracking"), WIKI("wiki"),
    WORK_PACKAGE_TRACKING("work_package_tracking");

    private String module;

    ModuleName(String module) {
        this.module = module;
    }

    public String getModule() {
        return module;
    }
}
