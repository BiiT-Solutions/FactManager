package com.biit.factmanager.persistence.entities;

import com.biit.database.encryption.StringCryptoConverter;
import com.biit.eventstructure.event.IKafkaStorable;
import com.biit.factmanager.persistence.entities.exceptions.FactValueInvalidException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.context.annotation.Primary;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(discriminatorType = DiscriminatorType.STRING, name = "type")
@Primary
@Table(name = "facts", indexes = {
        @Index(name = "ind_organization", columnList = "organization"),
        @Index(name = "ind_created_by", columnList = "created_by"),
        @Index(name = "ind_application", columnList = "application"),
        @Index(name = "ind_tenant", columnList = "tenant"),
        @Index(name = "ind_session", columnList = "session"),
        @Index(name = "ind_subject", columnList = "subject"),
        @Index(name = "ind_fact_type", columnList = "fact_type"),
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

    @Column(name = "application") //ReplyTo
    private String application;

    //Patient Id
    @Column(name = "tenant")  // Tenant
    private String tenant;

    @Column(name = "session")  // SessionId
    private String session;

    @Column(name = "subject")  // Subject
    private String subject;

    //Examination Name
    @Column(name = "grouping")
    private String group;

    //Answers // Timing // ...
    @Column(name = "fact_type")
    private String factType;

    @Column(name = "fact_value", length = MAX_JSON_LENGTH)
    @Convert(converter = StringCryptoConverter.class)
    private String value;

    // ID of the entity on the fact
    @Column(name = "element")
    @Convert(converter = StringCryptoConverter.class)
    private String element;

    @Column(name = "created_by")  //Issuer
    private String createdBy;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Transient
    private transient ENTITY entity;

    @OneToMany(mappedBy = "fact", orphanRemoval = true, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Collection<CustomProperty> customProperties;

    private static ObjectMapper objectMapper = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);

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

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
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

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
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
            setValue(objectMapper.writeValueAsString(entity));
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
                entity = objectMapper.readValue(getValue(), getJsonParser());
            } catch (JsonProcessingException e) {
                throw new FactValueInvalidException(e);
            }
        }
        return entity;
    }

    public Collection<CustomProperty> getCustomProperties() {
        return customProperties;
    }

    public void setCustomProperties(Collection<CustomProperty> customProperties) {
        this.customProperties = customProperties;
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
        if (!(o instanceof Fact<?> fact)) {
            return false;
        }

        if (getOrganization() != null ? !getOrganization().equals(fact.getOrganization()) : fact.getOrganization() != null) {
            return false;
        }
        if (getApplication() != null ? !getApplication().equals(fact.getApplication()) : fact.getApplication() != null) {
            return false;
        }
        if (getTenant() != null ? !getTenant().equals(fact.getTenant()) : fact.getTenant() != null) {
            return false;
        }
        if (getSession() != null ? !getSession().equals(fact.getSession()) : fact.getSession() != null) {
            return false;
        }
        if (getSubject() != null ? !getSubject().equals(fact.getSubject()) : fact.getSubject() != null) {
            return false;
        }
        if (getGroup() != null ? !getGroup().equals(fact.getGroup()) : fact.getGroup() != null) {
            return false;
        }
        if (getFactType() != null ? !getFactType().equals(fact.getFactType()) : fact.getFactType() != null) {
            return false;
        }
        if (getValue() != null ? !getValue().equals(fact.getValue()) : fact.getValue() != null) {
            return false;
        }
        return getElement() != null ? getElement().equals(fact.getElement()) : fact.getElement() == null;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOrganization(), getTenant(), getSession(), getSubject(), getGroup(), getFactType(),
                getValue(), getElement());
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
