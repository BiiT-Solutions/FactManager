package com.biit.factmanager.persistence.entities.values;

import com.biit.kafka.events.EventPayload;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class FormrunnerQuestionValue implements EventPayload {
    protected static final String DEFAULT_PATH_SEPARATOR = "/";

    private String answer;

    private String xpath;

    //Patient name.
    private String itemName;

    private String formVersion;

    private String formName;

    private Long professionalId;

    private Long companyId;

    public FormrunnerQuestionValue() {
        super();
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

    @JsonIgnore
    public String getQuestion() {
        if (getXpath() != null) {
            return getXpath().substring(getXpath().lastIndexOf(DEFAULT_PATH_SEPARATOR) + 1);
        }
        return "";
    }

    public String getFormVersion() {
        return formVersion;
    }

    public void setFormVersion(String formVersion) {
        this.formVersion = formVersion;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
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

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

}
