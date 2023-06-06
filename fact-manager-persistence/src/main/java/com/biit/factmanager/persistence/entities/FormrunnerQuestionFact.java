package com.biit.factmanager.persistence.entities;


import com.biit.eventstructure.event.IKafkaStorable;
import com.biit.factmanager.persistence.entities.values.FormrunnerQuestionValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;


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
                return switch (answer) {
                    case "A" -> "1";
                    case "B" -> "2";
                    case "C" -> "3";
                    case "D" -> "4";
                    default -> "5";
                };
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
        //This method must be redone.
        return 0;
    }
}
