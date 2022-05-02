package com.biit.factmanager.kafka;

import com.biit.factmanager.persistence.entities.FormrunnerQuestionFact;
import com.biit.kafka.EventDeserializer;

public class FactDeserializer extends EventDeserializer<FormrunnerQuestionFact> {

    public FactDeserializer() {
        super(FormrunnerQuestionFact.class);
    }
}
