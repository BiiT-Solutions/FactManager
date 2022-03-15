package com.biit.factmanager.kafka.consumers;

import com.biit.factmanager.core.providers.FactProvider;
import com.biit.factmanager.logger.FactManagerLogger;
import com.biit.factmanager.persistence.entities.FormRunnerFact;
import com.biit.kafka.consumers.EventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;

@EnableKafka
@Configuration
public class FormAnswerConsumerListeners2 extends EventListener<FormRunnerFact> {
    private final FactProvider<FormRunnerFact> factProvider;

    @Autowired
    public FormAnswerConsumerListeners2(FactProvider<FormRunnerFact> factProvider) {
        super();
        this.factProvider = factProvider;
    }

    @Override
    @KafkaListener(topics = "${kafka.topic}", groupId = "2", clientIdPrefix = "firstListener", containerFactory = "eventListenerContainerFactory")
    public void eventsListener(FormRunnerFact fact) {
        super.eventsListener(fact);
        final FormRunnerFact savedFact = factProvider.save(fact);
        FactManagerLogger.debug(this.getClass().getName(), "Saved fact " + savedFact.toString());
    }
}
