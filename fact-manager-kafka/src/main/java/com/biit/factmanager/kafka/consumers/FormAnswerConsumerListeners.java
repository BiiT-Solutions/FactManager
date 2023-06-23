package com.biit.factmanager.kafka.consumers;

import com.biit.factmanager.core.providers.FactProvider;
import com.biit.factmanager.logger.FactManagerLogger;
import com.biit.factmanager.persistence.entities.FormrunnerQuestionFact;
import com.biit.kafka.consumers.EventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;

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
    @KafkaListener(topics = "${spring.kafka.topic}", groupId = "${spring.kafka.group.id}", clientIdPrefix = "firstListener",
            containerFactory = "templateEventListenerContainerFactory")
    public void eventsListener(FormrunnerQuestionFact fact,
                               final @Header(KafkaHeaders.OFFSET) Integer offset,
                               final @Header(value = KafkaHeaders.KEY, required = false) String key,
                               final @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                               final @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                               final @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long timeStamp) {
        super.eventsListener(fact, offset, key, partition, topic, timeStamp);
        final FormrunnerQuestionFact savedFact = factProvider.save(fact);
        FactManagerLogger.debug(this.getClass().getName(), "Saved fact " + savedFact.toString());
    }
}
