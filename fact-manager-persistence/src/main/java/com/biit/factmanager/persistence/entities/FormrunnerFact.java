package com.biit.factmanager.persistence.entities;


import org.springframework.context.annotation.Primary;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.time.LocalDateTime;


@Entity
@XmlRootElement
@Primary
@DiscriminatorValue("FormRunnerFact")
@Table(name = "facts")
public class FormrunnerFact extends Fact<FormRunnerValue> {

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

    @Column(length = MAX_UNIQUE_COLUMN_LENGTH)
    private String category;

    @Column(length = MAX_UNIQUE_COLUMN_LENGTH)
    private String question;

    @Column(length = MAX_UNIQUE_COLUMN_LENGTH)
    private String answer;

    @Column(name = "score")
    private double score;

    @Column(length = MAX_UNIQUE_COLUMN_LENGTH)
    private String xpath;

    @Column(length = MAX_UNIQUE_COLUMN_LENGTH, name = "examination_name")
    private String examinationName;

    @Column(name = "examination_version")
    private long examinationVersion;

    @Column(name = "company_id")
    private long companyId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;


    public FormrunnerFact() {
        setCreatedAt(LocalDateTime.now());
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getXpath() {
        return xpath;
    }

    public void setXpath(String xpath) {
        this.xpath = xpath;
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

    public LocalDateTime getCreatedAt() {
        return createdAt == null ? null : LocalDateTime.now();
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt == null ? null : LocalDateTime.now();
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
                ", category=" + category +
                ", question=" + question +
                ", answer=" + answer +
                ", xpath=" + xpath +
                ", createdAt=" + createdAt +
                '}';
    }
}