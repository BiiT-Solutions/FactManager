package com.biit.factmanager.persistence.entities;

import javax.persistence.DiscriminatorValue;

@DiscriminatorValue("FormRunnerValue")
public class FormRunnerValue extends Fact<FormRunnerValue>{
    private String value;

    private String xpath;

    private double score;

    private String question;

    private String examinationName;

    private String examinationVersion;

    public FormRunnerValue() {}

    @Override
    public void setValue (String string) {
        value = string;
    }

    public String getValue() {
        return value;
    }

    public String getXpath() {
        return xpath;
    }

    public void setXpath(String xpath) {
        this.xpath = xpath;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getExaminationName() {
        return examinationName;
    }

    public void setExaminationName(String examinationName) {
        this.examinationName = examinationName;
    }

    public String getExaminationVersion() {
        return examinationVersion;
    }

    public void setExaminationVersion(String examinationVersion) {
        this.examinationVersion = examinationVersion;
    }
}
