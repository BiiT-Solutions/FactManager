package com.biit.factmanager.kafka.consumers;

import com.biit.factmanager.core.providers.FactProvider;
import com.biit.factmanager.logger.FactManagerLogger;
import com.biit.factmanager.persistence.entities.CustomProperty;
import com.biit.factmanager.persistence.entities.LogFact;
import com.biit.kafka.config.ObjectMapperFactory;
import com.biit.kafka.consumers.EventListener;
import com.biit.kafka.events.Event;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Controller;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;


@Controller
public class EventController {
    private static final String ORGANIZATION = "organization";
    private static final String PROCESS = "process";

    private final EventListener eventListener;
    private final EventConsumerListener eventConsumerListener;

    private final FactProvider<LogFact> factProvider;
    private final ObjectMapper objectMapper;


    public EventController(EventListener eventListener, EventConsumerListener eventConsumerListener,
                           FactProvider<LogFact> factProvider, ObjectMapper objectMapper) {
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
            final LogFact savedFact = factProvider.save(convert(event, topic));
            FactManagerLogger.debug(this.getClass().getName(), "Saved fact " + savedFact.toString());
        });
    }


    private LogFact convert(Event event, String topic) {
        final LogFact logFact = new LogFact();
        logFact.setCreatedBy(event.getCreatedBy());
        logFact.setApplication(event.getReplyTo());
        logFact.setTenant(event.getTenant());
        logFact.setSubject(event.getSubject());
        logFact.setSession(String.valueOf(event.getSessionId()));
        //logFact.setFactType();
        logFact.setGroup(topic);
        logFact.setElement(event.getMessageId() != null ? event.getMessageId().toString() : null);
        try {
            logFact.setValue(ObjectMapperFactory.getObjectMapper().writeValueAsString(event.getPayload()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        if (event.getCreatedAt() != null) {
            logFact.setCreatedAt(event.getCreatedAt());
        }
        if (event.getCustomProperties() != null) {
            logFact.setOrganization(event.getCustomProperties().get(ORGANIZATION));

            final List<CustomProperty> customProperties = new ArrayList<>();
            for (final Map.Entry<String, String> entry : event.getCustomProperties().entrySet()) {
                customProperties.add(new CustomProperty(logFact, entry.getKey(), entry.getValue()));
            }
            logFact.setCustomProperties(customProperties);
        }
        return logFact;
    }
}
