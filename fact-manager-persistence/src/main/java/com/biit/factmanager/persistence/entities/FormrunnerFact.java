package com.biit.factmanager.persistence.entities;


import org.springframework.context.annotation.Primary;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.time.LocalDateTime;


@Entity
@XmlRootElement
@Primary
@Table(name = "formrunner_facts")
public class FormrunnerFact {

    public static final int MAX_UNIQUE_COLUMN_LENGTH = 190;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tenant_id")
    private long tenantId;

    @Column(length = MAX_UNIQUE_COLUMN_LENGTH)
    private String category;

    @Column(name = "value")
    private String value;

    @Column(name = "element_id")
    private String elementId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public FormrunnerFact() {
        setCreatedAt(LocalDateTime.now());
    }

    public Integer getId() {
        return id;
    }

    public long getTenantId() {
        return tenantId;
    }

    public void setTenantId(long tenantId) {
        this.tenantId = tenantId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Fact{" +
                "id=" + id +
                ", tenantId=" + tenantId +
                ", category=" + category +
                ", value=" + value +
                ", category=" + category +
                ", elementId=" + elementId +
                ", createdAt=" + createdAt +
                '}';
    }
}
