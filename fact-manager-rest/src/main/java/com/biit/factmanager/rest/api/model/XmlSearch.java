package com.biit.factmanager.rest.api.model;

import java.time.OffsetDateTime;
import java.util.List;

public class XmlSearch {
    private String organization;
    private String unit;
    private List<String> createdBy;
    private String application;
    private String tenant;
    private String session;
    private String group;
    private String element;
    private String elementName;
    private OffsetDateTime from;
    private OffsetDateTime to;
    private Integer lastDays;
    private Boolean latestByUser;

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public List<String> getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(List<String> createdBy) {
        this.createdBy = createdBy;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getElement() {
        return element;
    }

    public void setElement(String element) {
        this.element = element;
    }

    public String getElementName() {
        return elementName;
    }

    public void setElementName(String elementName) {
        this.elementName = elementName;
    }

    public OffsetDateTime getFrom() {
        return from;
    }

    public void setFrom(OffsetDateTime from) {
        this.from = from;
    }

    public OffsetDateTime getTo() {
        return to;
    }

    public void setTo(OffsetDateTime to) {
        this.to = to;
    }

    public Integer getLastDays() {
        return lastDays;
    }

    public void setLastDays(Integer lastDays) {
        this.lastDays = lastDays;
    }

    public Boolean getLatestByUser() {
        return latestByUser;
    }

    public void setLatestByUser(Boolean latestByUser) {
        this.latestByUser = latestByUser;
    }
}
