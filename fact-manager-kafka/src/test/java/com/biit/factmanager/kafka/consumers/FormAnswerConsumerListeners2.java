package com.biit.factmanager.kafka.consumers;

import com.biit.factmanager.core.providers.FactProvider;
import com.biit.factmanager.kafka.producers.FormrunnerQuestionFactEvent;
import com.biit.factmanager.logger.FactManagerLogger;
import com.biit.factmanager.persistence.entities.FormrunnerQuestionFact;
import com.biit.kafka.consumers.EventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;

@EnableKafka
@Configuration
public class FormAnswerConsumerListeners2 extends EventListener<FormrunnerQuestionFact> {
    private final FactProvider<FormrunnerQuestionFact> factProvider;

    @Autowired
    public FormAnswerConsumerListeners2(FactProvider<FormrunnerQuestionFact> factProvider) {
        super();
        this.factProvider = factProvider;
    }

    @KafkaListener(topics = "${kafka.topic}", groupId = "2", clientIdPrefix = "firstListener", containerFactory = "templateEventListenerContainerFactory")
    public void eventsListener(FormrunnerQuestionFact fact) {
        final FormrunnerQuestionFact savedFact = factProvider.save(fact);
        FactManagerLogger.debug(this.getClass().getName(), "Saved fact " + savedFact.toString());
    }
}
