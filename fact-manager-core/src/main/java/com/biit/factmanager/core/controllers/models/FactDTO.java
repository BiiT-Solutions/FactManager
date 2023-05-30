package com.biit.factmanager.core.controllers.models;

import com.biit.server.controllers.models.ElementDTO;

import java.time.LocalDateTime;

public class FactDTO<ENTITY> extends ElementDTO {

    private String organization;

    private String customer;

    private String application;

    private String tenant;

    private String tag;

    private String group;

    private String value;

    private String element;

    private LocalDateTime createdAt;

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getElement() {
        return element;
    }

    public void setElement(String element) {
        this.element = element;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }


}
