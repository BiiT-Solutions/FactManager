package com.biit.factmanager.persistence.entities;

import com.biit.database.encryption.StringCryptoConverter;
import com.biit.factmanager.persistence.entities.exceptions.FactValueInvalidException;
import com.biit.factmanager.persistence.entities.exceptions.ValueAlreadySet;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Primary;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(discriminatorType = DiscriminatorType.STRING, name = "type")
@Primary
@Table(name = "facts")
public abstract class Fact<Value> implements IPivotViewerData {
    private static final int MAX_JSON_LENGTH = 100000;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "organization_id")
    private String organizationId;

    //Patient Id
    @Column(name = "tenant_id")
    private String tenantId;

    @Column(name = "tag")
    private String tag;

    @Column(name = "grouping")
    private String group;

    @Column(name = "value", length = MAX_JSON_LENGTH)
    @Convert(converter = StringCryptoConverter.class)
    private String value;

    // Id of the entity on the fact
    @Column(name = "element_id")
    @Convert(converter = StringCryptoConverter.class)
    private String elementId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Fact() {
        setCreatedAt(LocalDateTime.now());
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getValue() {
        return value == null ? "" : value;
    }

    protected void setValue(String value) {
        if (this.value != null) {
            throw new ValueAlreadySet();
        }
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    protected void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String getPivotViewerValueItemId() {
        return tenantId;
    }

    @Override
    public String getPivotViewerItemName() {
        return tenantId;
    }

    public void setEntity(Value entity) {
        try {
            setValue(new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL).writeValueAsString(entity));
        } catch (JsonProcessingException e) {
            throw new FactValueInvalidException(e);
        }
    }

    public Value getEntity() {
        try {
            return new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL).readValue(getValue(), new TypeReference<Value>() {
            });
        } catch (JsonProcessingException e) {
            throw new FactValueInvalidException(e);
        }
    }
}
