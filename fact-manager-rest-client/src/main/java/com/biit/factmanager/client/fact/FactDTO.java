package com.biit.factmanager.client.fact;

import com.biit.factmanager.client.ObjectMapperFactory;
import com.biit.factmanager.logger.FactManagerLogger;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FactDTO {

    private Long id;

    private String organization;

    private String customer;

    private String application;

    private String tenant;

    private String session;

    private String subject;

    private String group;

    private String factType;

    private String element;

    private String createdBy;

    private LocalDateTime createdAt;

    @JsonSerialize(using = JsonValueSerializer.class)
    @JsonDeserialize(using = JsonValueDeserializer.class)
    private String value;

    private String valueType;

    public FactDTO() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
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

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    @JsonIgnore
    public Object getEntityObject() throws ClassNotFoundException, JsonProcessingException {
        return ObjectMapperFactory.getObjectMapper().readValue(getValue(), Class.forName(getValueType()));
    }

    @JsonIgnore
    public void setEntity(Object entityAsJson) {
        try {
            setValue(ObjectMapperFactory.getObjectMapper().writeValueAsString(entityAsJson));
        } catch (JsonProcessingException e) {
            FactManagerLogger.errorMessage(this.getClass(), e);
        }
    }
}
