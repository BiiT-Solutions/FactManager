package com.biit.factmanager.kafka.producers;

import com.biit.factmanager.persistence.entities.FormrunnerQuestionFact;
import com.biit.kafka.events.Event;
import com.fasterxml.jackson.core.type.TypeReference;

public class FormrunnerQuestionFactEvent extends Event<FormrunnerQuestionFact> {

    public FormrunnerQuestionFactEvent() {
    }


    public FormrunnerQuestionFactEvent(FormrunnerQuestionFact payload) {
        super(payload);
    }

    @Override
    protected TypeReference<FormrunnerQuestionFact> getJsonParser() {
        return new TypeReference<>() {
        };
    }
}
