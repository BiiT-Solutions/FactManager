package com.biit.factmanager.persistence.entities;


import org.springframework.context.annotation.Primary;

import javax.persistence.*;
import java.util.Date;


@Entity
@Primary
@Table(name = "examination_finished_fact")
public class ExaminationFinishedFact {

    public static final int MAX_UNIQUE_COLUMN_LENGTH = 190;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @Column(name = "patient_id")
    private long patientId;

    @Column(name = "organization_id")
    private long organizationId;

    @Column(name = "professional_id")
    private long professionalId;

    @Column(length = MAX_UNIQUE_COLUMN_LENGTH, name = "examination_name")
    private String examinationName;

    @Column(name = "examination_version")
    private long examinationVersion;

    @Column(name = "company_id")
    private long companyId;

    @Column(name = "created_at")
    private Date createdAt;


    public ExaminationFinishedFact() {
        setCreatedAt(new Date());
    }

    public long getPatientId() {
        return patientId;
    }

    public void setPatientId(long patientId) {
        this.patientId = patientId;
    }

    public long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(long organizationId) {
        this.organizationId = organizationId;
    }

    public long getProfessionalId() {
        return professionalId;
    }

    public void setProfessionalId(long professionalId) {
        this.professionalId = professionalId;
    }

    public String getExaminationName() {
        return examinationName;
    }

    public void setExaminationName(String examinationName) {
        this.examinationName = examinationName;
    }

    public long getExaminationVersion() {
        return examinationVersion;
    }

    public void setExaminationVersion(long examinationVersion) {
        this.examinationVersion = examinationVersion;
    }

    public long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(long companyId) {
        this.companyId = companyId;
    }

    public Date getCreatedAt() {
        return createdAt == null ? null : new Date(createdAt.getTime());
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt == null ? null : new Date(createdAt.getTime());
    }



    public Integer getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Fact{" +
                "id=" + id +
                ", patientId=" + patientId +
                ", companyId=" + companyId +
                ", organizationId=" + organizationId +
                ", professionalId=" + professionalId +
                ", examinationVersion=" + examinationVersion +
                ", examinationName=" + examinationName +
                ", createdAt=" + createdAt +
                '}';
    }

}
