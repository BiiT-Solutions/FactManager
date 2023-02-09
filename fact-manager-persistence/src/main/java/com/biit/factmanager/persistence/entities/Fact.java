package com.biit.factmanager.persistence.entities;

import com.biit.database.encryption.LocalDateTimeCryptoConverter;
import com.biit.database.encryption.StringCryptoConverter;
import com.biit.eventstructure.event.IKafkaStorable;
import com.biit.factmanager.persistence.entities.exceptions.FactValueInvalidException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.context.annotation.Primary;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(discriminatorType = DiscriminatorType.STRING, name = "type")
@Primary
@Table(name = "facts", indexes = {
        @Index(name = "ind_organization", columnList = "organization"),
        @Index(name = "ind_customer", columnList = "customer"),
        @Index(name = "ind_application", columnList = "application"),
        @Index(name = "ind_tenant", columnList = "tenant"),
        @Index(name = "ind_process", columnList = "process"),
        @Index(name = "ind_tag", columnList = "tag"),
        @Index(name = "ind_group", columnList = "grouping"),
        @Index(name = "ind_element", columnList = "element"),
})
public abstract class Fact<ENTITY> implements IPivotViewerData, IKafkaStorable {
    private static final int MAX_JSON_LENGTH = 10 * 1024 * 1024;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "organization")
    private String organization;

    @Column(name = "customer")
    private String customer;

    @Column(name = "application")
    private String application;

    //Patient Id
    @Column(name = "tenant")
    private String tenant;

    @Column(name = "process")
    private String process;

    @Column(name = "tag")
    private String tag;

    //Examination Name
    @Column(name = "grouping")
    private String group;

    @Column(name = "value", length = MAX_JSON_LENGTH)
    @Convert(converter = StringCryptoConverter.class)
    private String value;

    // ID of the entity on the fact
    @Column(name = "element")
    @Convert(converter = StringCryptoConverter.class)
    private String element;

    @CreationTimestamp
    @Column(name = "created_at")
    @Convert(converter = LocalDateTimeCryptoConverter.class)
    private LocalDateTime createdAt;

    @Transient
    private transient ENTITY entity;

    public Fact() {
        super();
    }

    public Fact(ENTITY entity) {
        super();
        setEntity(entity);
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

    public void setValue(String value) {
        this.value = value;
        this.entity = null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
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

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getProcess() {
        return process;
    }

    public void setProcess(String process) {
        this.process = process;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    @Override
    @JsonIgnore
    public String getPivotViewerValueItemId() {
        return tenant;
    }

    @Override
    @JsonIgnore
    public String getPivotViewerItemName() {
        return tenant;
    }

    @JsonIgnore
    public void setEntity(ENTITY entity) {
        try {
            setValue(new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL).writeValueAsString(entity));
            this.entity = entity;
        } catch (JsonProcessingException e) {
            throw new FactValueInvalidException(e);
        }
    }

    protected abstract TypeReference<ENTITY> getJsonParser();

    @JsonIgnore
    public ENTITY getEntity() {
        if (getValue() != null && !getValue().isEmpty()) {
            try {
                entity = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL).readValue(getValue(), getJsonParser());
            } catch (JsonProcessingException e) {
                throw new FactValueInvalidException(e);
            }
        }
        return entity;
    }

    @Override
    @JsonIgnore
    public String getEventId() {
        return getElement();
    }

    @Override
    @JsonIgnore
    public LocalDateTime getCreationTime() {
        return getCreatedAt();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Fact<?> fact = (Fact<?>) o;
        return getCreatedAt().equals(fact.getCreatedAt()) && Objects.equals(getOrganization(), fact.getOrganization()) &&
                Objects.equals(getTenant(), fact.getTenant()) && Objects.equals(getTag(), fact.getTag()) &&
                Objects.equals(getGroup(), fact.getGroup()) && Objects.equals(getElement(), fact.getElement()) &&
                getValue().equals(fact.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOrganization(), getTenant(), getTag(), getGroup(), getValue(), getElement(), getCreatedAt());
    }

    @Override
    public String toString() {
        return getId() == null ? "Fact(" + getCreatedAt() + ")" : "Fact(" + getId() + ")";
    }

    @Transient
    @JsonIgnore
    public String getDiscriminatorValue() {
        final DiscriminatorValue val = this.getClass().getAnnotation(DiscriminatorValue.class);
        return val == null ? null : val.value();
    }
}
