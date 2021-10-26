package com.biit.factmanager.persistence.entities;

import com.biit.database.encryption.StringCryptoConverter;
import com.biit.factmanager.persistence.entities.exceptions.FactValueInvalidException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Primary;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Primary
@Table(name = "facts")
public abstract class Fact<T> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tenant_id")
    private long tenantId;

    @Column(name = "category")
    private String category;

    @Column(name = "value")
    @Convert(converter = StringCryptoConverter.class)
    private String value;

    @Column(name = "element_id")
    @Convert(converter = StringCryptoConverter.class)
    private String elementId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Fact() {
        setCreatedAt(LocalDateTime.now());
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    protected String getValue() {
        return value;
    }

    protected void setValue(String category) {
        this.category = category;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public long getTenantId() {
        return tenantId;
    }

    public void setTenantId(long tenantId) {
        this.tenantId = tenantId;
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

    private void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setEntity(T entity) {
        try {
            setValue(new ObjectMapper().writeValueAsString(getValue()));
        } catch (JsonProcessingException e) {
            throw new FactValueInvalidException(e);
        }
    }

    public T getEntity() {
        try {
            return new ObjectMapper().readValue(getValue(), new TypeReference<T>() {
            });
        } catch (JsonProcessingException e) {
            throw new FactValueInvalidException(e);
        }
    }
}
