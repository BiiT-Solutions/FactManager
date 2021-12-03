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
import java.lang.reflect.ParameterizedType;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * A Generic Fact with basic classification.
 *
 * @param <Value> All facts values must be composed only by primitive parameters.
 */

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

    // Kafka Tag
    @Column(name = "tag")
    private String tag;

    //Group is reserved by h2
    @Column(name = "group_by")
    private String group;

    //@Column(name = "value", length = MAX_JSON_LENGTH)
    //@Convert(converter = HashMapConverter.class)
    @ElementCollection
    @CollectionTable(name = "parameters", joinColumns = @JoinColumn(name = "parameters_id", referencedColumnName = "id"))
    @Column(name = "entity")
    private Map<String, String> value;

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

    protected Map<String, String> getValue() {
        return value;
    }

    protected void setValue(Map<String, String> value) {
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

    public void setCreatedAt(LocalDateTime createdAt) {
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
        setValue(new ObjectMapper().convertValue(entity, Map.class));
    }

    public Value getEntity() {
        return new ObjectMapper().convertValue(getValue(), (Class<Value>)
                ((ParameterizedType) getClass()
                        .getGenericSuperclass())
                        .getActualTypeArguments()[0]);

    }
}
