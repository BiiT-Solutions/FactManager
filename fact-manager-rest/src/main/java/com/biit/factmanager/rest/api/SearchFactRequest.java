package com.biit.factmanager.rest.api;

import com.biit.factmanager.dto.CustomPropertyDTO;
import com.biit.server.controllers.models.ElementDTO;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;

public class SearchFactRequest {
    @Size(min = ElementDTO.MIN_FIELD_LENGTH, max = ElementDTO.MAX_NORMAL_FIELD_LENGTH)
    private String organization;
    @Size(min = ElementDTO.MIN_FIELD_LENGTH, max = ElementDTO.MAX_NORMAL_FIELD_LENGTH)
    private String unit;
    private List<String> createdBy;
    @Size(min = ElementDTO.MIN_FIELD_LENGTH, max = ElementDTO.MAX_NORMAL_FIELD_LENGTH)
    private String application;
    @Size(min = ElementDTO.MIN_FIELD_LENGTH, max = ElementDTO.MAX_NORMAL_FIELD_LENGTH)
    private String tenant;
    @Size(min = ElementDTO.MIN_FIELD_LENGTH, max = ElementDTO.MAX_NORMAL_FIELD_LENGTH)
    private String session;
    @Size(min = ElementDTO.MIN_FIELD_LENGTH, max = ElementDTO.MAX_NORMAL_FIELD_LENGTH)
    private String subject;
    @Size(min = ElementDTO.MIN_FIELD_LENGTH, max = ElementDTO.MAX_NORMAL_FIELD_LENGTH)
    private String group;
    @Size(min = ElementDTO.MIN_FIELD_LENGTH, max = ElementDTO.MAX_NORMAL_FIELD_LENGTH)
    private String element;
    @Size(min = ElementDTO.MIN_FIELD_LENGTH, max = ElementDTO.MAX_NORMAL_FIELD_LENGTH)
    private String elementName;
    @Size(min = ElementDTO.MIN_FIELD_LENGTH, max = ElementDTO.MAX_NORMAL_FIELD_LENGTH)
    private String factType;
    private OffsetDateTime from;
    private OffsetDateTime to;
    private Integer lastDays;
    private Boolean latestByUser;
    private Collection<CustomPropertyDTO> customProperties;
    private List<String> valueParameters;

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

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
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

    public String getFactType() {
        return factType;
    }

    public void setFactType(String factType) {
        this.factType = factType;
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

    public Collection<CustomPropertyDTO> getCustomProperties() {
        return customProperties;
    }

    public void setCustomProperties(Collection<CustomPropertyDTO> customProperties) {
        this.customProperties = customProperties;
    }

    public List<String> getValueParameters() {
        return valueParameters;
    }

    public void setValueParameters(List<String> valueParameters) {
        this.valueParameters = valueParameters;
    }
}
