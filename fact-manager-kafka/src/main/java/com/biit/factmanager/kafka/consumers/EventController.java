package com.biit.factmanager.kafka.consumers;

import com.biit.factmanager.core.providers.FactProvider;
import com.biit.factmanager.logger.FactManagerLogger;
import com.biit.factmanager.persistence.entities.StringFact;
import com.biit.kafka.consumers.EventListener;
import com.biit.kafka.events.Event;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Controller;


@Controller
public class EventController {
    private static final String ORGANIZATION = "organization";
    private static final String PROCESS = "process";

    private final EventListener eventListener;
    private final StringEventConsumerListener stringEventConsumerListener;

    private final FactProvider<StringFact> factProvider;
    private final ObjectMapper objectMapper;


    public EventController(EventListener eventListener, StringEventConsumerListener stringEventConsumerListener,
                           FactProvider<StringFact> factProvider, ObjectMapper objectMapper) {
        this.eventListener = eventListener;
        this.stringEventConsumerListener = stringEventConsumerListener;
        this.factProvider = factProvider;
        this.objectMapper = objectMapper;

        //Listen to topic
        eventListener.addListener((event, offset, key, partition, topic, timeStamp) -> {

        });

        //Listens to all events on Kafka Streams.
        stringEventConsumerListener.addListener((event, offset, key, partition, topic, timeStamp) -> {
            final StringFact savedFact = factProvider.save(convert(event, topic));
            FactManagerLogger.debug(this.getClass().getName(), "Saved fact " + savedFact.toString());
        });
    }


    private StringFact convert(Event event, String topic) {
        final StringFact stringFact = new StringFact();
        stringFact.setCreatedBy(event.getCreatedBy());
        stringFact.setApplication(event.getReplyTo());
        stringFact.setTenant(event.getTenant());
        stringFact.setTag(event.getSubject());
        stringFact.setGroup(topic);
        stringFact.setElement(event.getMessageId() != null ? event.getMessageId().toString() : null);
        stringFact.setValue(event.getPayload());
        if (event.getCreatedAt() != null) {
            stringFact.setCreatedAt(event.getCreatedAt());
        }
        if (event.getCustomProperties() != null) {
            stringFact.setOrganization(event.getCustomProperties().get(ORGANIZATION));
            stringFact.setProcess(event.getCustomProperties().get(PROCESS));
        }
        return stringFact;
    }
}
