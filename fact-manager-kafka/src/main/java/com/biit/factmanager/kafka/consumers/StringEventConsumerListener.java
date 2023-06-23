package com.biit.factmanager.kafka.consumers;

import com.biit.factmanager.core.providers.FactProvider;
import com.biit.factmanager.kafka.models.StringEvent;
import com.biit.factmanager.logger.FactManagerLogger;
import com.biit.factmanager.persistence.entities.StringFact;
import com.biit.kafka.consumers.EventListener;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;

@EnableKafka
@Configuration
public class StringEventConsumerListener extends EventListener<StringEvent> {
    private static final String ORGANIZATION = "organization";
    private static final String PROCESS = "process";
    private final FactProvider<StringFact> factProvider;
    private final ObjectMapper objectMapper;

    @Autowired
    public StringEventConsumerListener(FactProvider<StringFact> factProvider, ObjectMapper objectMapper) {
        super();
        this.factProvider = factProvider;
        this.objectMapper = objectMapper;
    }

    @Override
    @KafkaListener(topicPattern = ".*", groupId = "${spring.kafka.group.id}", clientIdPrefix = "firstListener",
            containerFactory = "templateEventListenerContainerFactory")
    public void eventsListener(StringEvent event,
                               final @Header(KafkaHeaders.OFFSET) Integer offset,
                               final @Header(value = KafkaHeaders.KEY, required = false) String key,
                               final @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                               final @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                               final @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long timeStamp) {
        super.eventsListener(event, offset, key, partition, topic, timeStamp);
        final StringFact savedFact = factProvider.save(convert(event, topic));
        FactManagerLogger.debug(this.getClass().getName(), "Saved fact " + savedFact.toString());
    }

    private StringFact convert(StringEvent event, String topic) {
        final StringFact stringFact = new StringFact();
        stringFact.setIssuer(event.getIssuer());
        stringFact.setApplication(event.getReplyTo());
        stringFact.setTenant(event.getTenant());
        stringFact.setTag(event.getSubject());
        stringFact.setGroup(topic);
        stringFact.setElement(event.getMessageId() != null ? event.getMessageId().toString() : null);
        stringFact.setValue(event.getPayload());
        if (event.getCreateAt() != null) {
            stringFact.setCreatedAt(event.getCreateAt());
        }
        if (event.getCustomProperties() != null) {
            stringFact.setOrganization(event.getCustomProperties().get(ORGANIZATION));
            stringFact.setProcess(event.getCustomProperties().get(PROCESS));
        }
        return stringFact;
    }

}
