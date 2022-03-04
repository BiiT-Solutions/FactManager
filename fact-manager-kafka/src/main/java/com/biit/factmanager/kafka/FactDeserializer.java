package com.biit.factmanager.kafka;

import com.biit.factmanager.persistence.entities.FormRunnerFact;
import com.biit.kafka.EventDeserializer;

public class FactDeserializer extends EventDeserializer<FormRunnerFact> {

    public FactDeserializer() {
        super(FormRunnerFact.class);
    }
}
