package com.biit.factmanager.kafka.consumers;

import com.biit.factmanager.core.providers.FactProvider;
import com.biit.factmanager.persistence.entities.FormRunnerFact;
import com.biit.factmanager.persistence.entities.values.FormRunnerValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FormAnswerConsumer extends FactConsumer<FormRunnerValue, FormRunnerFact> {

    @Autowired
    public FormAnswerConsumer(FactProvider<FormRunnerFact> factProvider) {
        super(FormRunnerFact.class, factProvider);
    }
}
