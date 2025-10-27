package com.biit.factmanager.persistence.entities;

/*-
 * #%L
 * FactManager (Persistence)
 * %%
 * Copyright (C) 2020 - 2025 BiiT Sourcing Solutions S.L.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.biit.database.encryption.StringCryptoConverter;
import com.biit.eventstructure.event.IKafkaStorable;
import com.biit.factmanager.logger.FactManagerLogger;
import com.biit.factmanager.persistence.entities.exceptions.FactValueInvalidException;
import com.biit.kafka.config.ObjectMapperFactory;
import com.biit.server.persistence.entities.CreatedElement;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
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
import org.springframework.context.annotation.Primary;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
        @Index(name = "ind_element_name", columnList = "element_name"),
})
public class Fact<ENTITY> extends CreatedElement implements IPivotViewerData, IKafkaStorable {
    private static final int MAX_JSON_LENGTH = 10 * 1024 * 1024;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "organization")
    private String organization;

    //Team, Department, ...
    @Column(name = "unit")
    private String unit;

    @Column(name = "application") //ReplyTo
    private String application;

    //Patient Id
    @Column(name = "tenant")  // Tenant
    private String tenant;

    @Column(name = "session")  // SessionId
    private String session;

    @Column(name = "subject")  // Subject
    private String subject;

    //Topic
    @Column(name = "grouping")
    private String group;

    //Answers // Timing // ...
    @Column(name = "fact_type")
    private String factType;

    @Column(name = "fact_value", length = MAX_JSON_LENGTH)
    @Convert(converter = StringCryptoConverter.class)
    private String value;

    //The name of the form, customer, patient, etc.
    @Column(name = "element_name")
    private String elementName;

    // ID of the entity on the fact
    @Column(name = "element")
    private String element;

    @Transient
    private transient ENTITY entity;

    @OneToMany(mappedBy = "fact", orphanRemoval = true, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Collection<CustomProperty> customProperties;

    public Fact() {
        super();
        customProperties = new ArrayList<>();
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

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getElementName() {
        return elementName;
    }

    public void setElementName(String elementName) {
        this.elementName = elementName;
    }

    public String getElement() {
        return element;
    }

    public void setElement(String element) {
        this.element = element;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
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
    public String getPivotViewerTag() {
        return null;
    }

    @Override
    public String getPivotViewerValue() {
        return null;
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

    @Override
    public Integer getPivotViewerItemImageIndex() {
        return null;
    }

    @JsonIgnore
    public void setEntity(ENTITY entity) {
        try {
            setValue(ObjectMapperFactory.getObjectMapper().writeValueAsString(entity));
            this.entity = entity;
        } catch (JsonProcessingException e) {
            throw new FactValueInvalidException(e);
        }
    }

    protected TypeReference<ENTITY> getJsonParser() {
        return new TypeReference<>() {
        };
    }

    @JsonIgnore
    public ENTITY getEntity() {
        if (getValue() != null && !getValue().isEmpty()) {
            try {
                entity = ObjectMapperFactory.getObjectMapper().readValue(getValue(), getJsonParser());
            } catch (JsonProcessingException e) {
                FactManagerLogger.errorMessage(this.getClass(), e);
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
