package com.biit.factmanager.persistence.entities;


import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;
import java.time.LocalDateTime;


@Entity
@DiscriminatorValue("FormRunnerFact")
public class FormrunnerFact extends Fact<FormRunnerValue> {

    @Transient
    private final FormRunnerValue formRunnerValue;

    public FormrunnerFact() {
        formRunnerValue = new FormRunnerValue();
    }

    @Override
    public void setCreatedAt(LocalDateTime localDateTime) {
        super.setCreatedAt(localDateTime);
    }

    public void setXPath(String xpath) {
        formRunnerValue.setXpath(xpath);
    }

    public String getXpath() {
        return formRunnerValue.getXpath();
    }

    public void setScore(double score) {
        formRunnerValue.setScore(score);
    }

    public double getScore() {
        return formRunnerValue.getScore();
    }

    public void setQuestion(String question) {
        formRunnerValue.setQuestion(question);
    }

    public String getQuestion() {
        return formRunnerValue.getQuestion();
    }

    public void setExaminationName(String examinationName) {
        formRunnerValue.setExaminationName(examinationName);
    }

    public String getExaminationName() {
        return formRunnerValue.getExaminationName();
    }

    public void setExaminationVersion(String examinationVersion) {
        formRunnerValue.setExaminationVersion(examinationVersion);
    }

    public String getExaminationVersion() {
        return formRunnerValue.getExaminationVersion();
    }

    public void setPatientId(long patientId) {
        formRunnerValue.setPatientId(patientId);
    }

    public long getPatientId() {
        return formRunnerValue.getPatientId();
    }

    public void setOrganizationId(long organizationId) {
        formRunnerValue.setOrganizationId(organizationId);
    }

    public long getOrganizationId() {
        return formRunnerValue.getOrganizationId();
    }

    public void setProfessionalId(long professionalId) {
        formRunnerValue.setProfessionalId(professionalId);
    }

    public long getProfessionalId() {
        return formRunnerValue.getProfessionalId();
    }

    public void setCompanyId(long companyId) {
        formRunnerValue.setCompanyId(companyId);
    }

    public long getCompanyId() {
        return formRunnerValue.getCompanyId();
    }
}