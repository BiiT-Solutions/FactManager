package com.biit.factmanager.persistence.entities;


import com.biit.eventstructure.event.IKafkaStorable;
import com.biit.factmanager.persistence.entities.values.FormrunnerQuestionValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.type.TypeReference;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

/**
 * Tag is from Kafka
 * Group is ExaminationName
 * TenantId is Patient Id.
 * ElementId is Examination Id.
 * OrganizationId is Organization Id.
 */
@Entity
@DiscriminatorValue("FormRunnerQuestionFact")
public class FormrunnerQuestionFact extends Fact<FormrunnerQuestionValue> implements IKafkaStorable {

    @Transient
    private FormrunnerQuestionValue formrunnerQuestionValue;

    @JsonCreator
    public FormrunnerQuestionFact() {
        super();
        formrunnerQuestionValue = new FormrunnerQuestionValue();
    }

    private FormrunnerQuestionValue getFormrunnerQuestionValue() {
        if (formrunnerQuestionValue == null || formrunnerQuestionValue.getAnswer() == null) {
            formrunnerQuestionValue = getEntity();
        }
        return formrunnerQuestionValue;
    }

    @Override
    public void setEntity(FormrunnerQuestionValue entity) {
        formrunnerQuestionValue = entity;
        super.setEntity(entity);
    }

    @Override
    protected TypeReference<FormrunnerQuestionValue> getJsonParser() {
        return new TypeReference<FormrunnerQuestionValue>() {
        };
    }

    @Override
    public String getPivotViewerTag() {
        if (getFormrunnerQuestionValue() != null && getFormrunnerQuestionValue().getQuestion() != null) {
            return getFormrunnerQuestionValue().getQuestion();
        }
        return null;
    }

    @Override
    public String getPivotViewerValue() {
        if (getFormrunnerQuestionValue().getAnswer() != null || !getFormrunnerQuestionValue().getAnswer().isEmpty()) {
            final String answer = getFormrunnerQuestionValue().getAnswer();
            if ("A".compareTo(answer) == 0 || "B".compareTo(answer) == 0
                    || "C".compareTo(answer) == 0 || "D".compareTo(answer) == 0) {
                switch (answer) {
                    case "A":
                        return "1";
                    case "B":
                        return "2";
                    case "C":
                        return "3";
                    case "D":
                        return "4";
                    default:
                        return "5";
                }
            } else if (answer.matches("[+-]?\\d*(\\.\\d+)?")) {
                return getFormrunnerQuestionValue().getAnswer();
            } else {
                return null;
            }
        }
        return null;
    }

    @Override
    public String getPivotViewerItemName() {
        if (getFormrunnerQuestionValue() != null && getFormrunnerQuestionValue().getItemName() != null) {
            return getFormrunnerQuestionValue().getItemName();
        }
        return null;
    }

    @Override
    public Integer getPivotViewerItemImageIndex() {
        final String answer = getFormrunnerQuestionValue().getAnswer();
        if ("A".compareTo(answer) == 0 || "B".compareTo(answer) == 0
                || "C".compareTo(answer) == 0 || "D".compareTo(answer) == 0) {
            switch (answer) {
                case "A":
                    return 1;
                case "B":
                    return 2;
                case "C":
                    return 3;
                case "D":
                    return 4;
                default:
                    return 5;
            }
        } else if (answer.matches("[+-]?\\d*(\\.\\d+)?")) {
            final int numericAnswer = Integer.parseInt(answer);
            if (numericAnswer > 0 && numericAnswer <= 5) {
                return numericAnswer;
            }
            if (numericAnswer > 5) {
                return 5;
            } else {
                return 1;
            }
        } else {
            return null;
        }
    }
}