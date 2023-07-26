package com.biit.factmanager.dto;

import com.biit.server.controllers.models.ElementDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.time.LocalDateTime;
import java.util.Collection;

public class FactDTO extends ElementDTO {

    private String organization;

    private String application;

    private String tenant;

    private String session;

    private String subject;

    private String group;

    private String factType;

    @JsonSerialize(using = JsonValueSerializer.class)
    @JsonDeserialize(using = JsonValueDeserializer.class)
    private String value;

    private String valueType;

    private String element;

    private String createdBy;

    private LocalDateTime createdAt;

    private Collection<CustomPropertyDTO> customProperties;

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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public Collection<CustomPropertyDTO> getCustomProperties() {
        return customProperties;
    }

    public void setCustomProperties(Collection<CustomPropertyDTO> customProperties) {
        this.customProperties = customProperties;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getFactType() {
        return factType;
    }

    public void setFactType(String factType) {
        this.factType = factType;
    }

    @Override
    public String getCreatedBy() {
        return createdBy;
    }

    @Override
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @JsonIgnore
    public Object getEntityObject() throws ClassNotFoundException, JsonProcessingException {
        return ObjectMapperFactory.getObjectMapper().readValue(getValue(), Class.forName(getValueType()));
    }
}
