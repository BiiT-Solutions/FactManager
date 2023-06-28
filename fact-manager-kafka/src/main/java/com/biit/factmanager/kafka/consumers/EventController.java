package com.biit.factmanager.kafka.consumers;

import com.biit.factmanager.core.providers.FactProvider;
import com.biit.factmanager.logger.FactManagerLogger;
import com.biit.factmanager.persistence.entities.StringFact;
import com.biit.kafka.config.ObjectMapperFactory;
import com.biit.kafka.consumers.EventListener;
import com.biit.kafka.events.Event;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Controller;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;


@Controller
public class EventController {
    private static final String ORGANIZATION = "organization";
    private static final String PROCESS = "process";

    private final EventListener eventListener;
    private final EventConsumerListener eventConsumerListener;

    private final FactProvider<StringFact> factProvider;
    private final ObjectMapper objectMapper;


    public EventController(EventListener eventListener, EventConsumerListener eventConsumerListener,
                           FactProvider<StringFact> factProvider, ObjectMapper objectMapper) {
        this.eventListener = eventListener;
        this.eventConsumerListener = eventConsumerListener;
        this.factProvider = factProvider;
        this.objectMapper = objectMapper;

        //Listen to topic
        eventListener.addListener((event, offset, key, partition, topic, timeStamp) -> {
            FactManagerLogger.debug(this.getClass(), "Received event '{}' on topic '{}', key '{}', partition '{}' at '{}'",
                    event, topic, key, partition, LocalDateTime.ofInstant(Instant.ofEpochMilli(timeStamp),
                            TimeZone.getDefault().toZoneId()));
        });

        //Listens to all events on Kafka Streams.
        eventConsumerListener.addListener((event, offset, key, partition, topic, timeStamp) -> {
            FactManagerLogger.debug(this.getClass(), "Received event '{}' on topic '{}', key '{}', partition '{}' at '{}'",
                    event, topic, key, partition, LocalDateTime.ofInstant(Instant.ofEpochMilli(timeStamp),
                            TimeZone.getDefault().toZoneId()));
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
        try {
            stringFact.setValue(ObjectMapperFactory.getObjectMapper().writeValueAsString(event.getPayload()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
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
