package com.biit.factmanager.kafka.consumers;

import com.biit.factmanager.core.providers.FactProvider;
import com.biit.factmanager.logger.FactManagerLogger;
import com.biit.factmanager.persistence.entities.FormrunnerQuestionFact;
import com.biit.kafka.consumers.EventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;

@EnableKafka
@Configuration
public class FormAnswerConsumerListeners extends EventListener<FormrunnerQuestionFact> {
    private final FactProvider<FormrunnerQuestionFact> factProvider;

    @Autowired
    public FormAnswerConsumerListeners(FactProvider<FormrunnerQuestionFact> factProvider) {
        super();
        this.factProvider = factProvider;
    }

    @Override
    @KafkaListener(topics = "${kafka.topic}", groupId = "1", clientIdPrefix = "firstListener", containerFactory = "eventListenerContainerFactory")
    public void eventsListener(FormrunnerQuestionFact fact) {
        super.eventsListener(fact);
        final FormrunnerQuestionFact savedFact = factProvider.save(fact);
        FactManagerLogger.debug(this.getClass().getName(), "Saved fact " + savedFact.toString());
    }
}
