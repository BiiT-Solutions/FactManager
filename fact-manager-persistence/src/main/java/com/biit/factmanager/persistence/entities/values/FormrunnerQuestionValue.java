package com.biit.factmanager.persistence.entities.values;

public class FormrunnerQuestionValue {
    private String answer;

    private String xpath;

    private String patientName;

    private Double score;

    private String question;

    private String parent;

    private String examinationVersion;

    private Long professionalId;

    private Long companyId;

    public FormrunnerQuestionValue() {
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getXpath() {
        return xpath;
    }

    public void setXpath(String xpath) {
        this.xpath = xpath;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getExaminationVersion() {
        return examinationVersion;
    }

    public void setExaminationVersion(String examinationVersion) {
        this.examinationVersion = examinationVersion;
    }

    public Long getProfessionalId() {
        return professionalId;
    }

    public void setProfessionalId(Long professionalId) {
        this.professionalId = professionalId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }
}
