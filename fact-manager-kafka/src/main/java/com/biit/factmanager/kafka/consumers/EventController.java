package com.biit.factmanager.kafka.consumers;

import com.biit.factmanager.core.providers.FactProvider;
import com.biit.factmanager.logger.FactManagerLogger;
import com.biit.factmanager.persistence.entities.CustomProperty;
import com.biit.factmanager.persistence.entities.LogFact;
import com.biit.kafka.consumers.EventListener;
import com.biit.kafka.events.Event;
import com.biit.kafka.events.EventCustomProperties;
import com.biit.kafka.logger.EventsLogger;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;


@Controller
public class EventController {
    private final EventListener eventListener;
    private final EventConsumerListener eventConsumerListener;

    private final FactProvider<LogFact> factProvider;
    private final ObjectMapper objectMapper;


    public EventController(@Autowired(required = false) EventListener eventListener,
                           @Autowired(required = false) EventConsumerListener eventConsumerListener,
                           FactProvider<LogFact> factProvider, ObjectMapper objectMapper) {
        this.eventListener = eventListener;
        this.eventConsumerListener = eventConsumerListener;
        this.factProvider = factProvider;
        this.objectMapper = objectMapper;

        //Listen to topic
        if (eventListener != null) {
            eventListener.addListener((event, offset, groupId, key, partition, topic, timeStamp) -> {
                EventsLogger.debug(this.getClass(), "Received event '{}' on topic '{}', key '{}', partition '{}' at '{}'",
                        event, topic, groupId, key, partition, LocalDateTime.ofInstant(Instant.ofEpochMilli(timeStamp),
                                TimeZone.getDefault().toZoneId()));
            });
        }

        //Listens to all events on Kafka Streams.
        if (eventConsumerListener != null) {
            eventConsumerListener.addListener((event, offset, groupId, key, partition, topic, timeStamp) -> {
                EventsLogger.debug(this.getClass(), "Received event '{}' on topic '{}', key '{}', partition '{}' at '{}'",
                        event, topic, groupId, key, partition, LocalDateTime.ofInstant(Instant.ofEpochMilli(timeStamp),
                                TimeZone.getDefault().toZoneId()));
                if (event != null) {
                    final LogFact savedFact = factProvider.save(convert(event, topic));
                    EventsLogger.debug(this.getClass().getName(), "Saved fact " + savedFact.toString());
                } else {
                    FactManagerLogger.warning(this.getClass(), "Receiving null event! Fact cannot be saved.");
                }
            });
        }
    }


    private LogFact convert(Event event, String topic) {
        if (event == null) {
            FactManagerLogger.warning(this.getClass(), "Receiving null event!");
            return null;
        }
        final LogFact logFact = new LogFact();
        logFact.setCreatedBy(event.getCreatedBy());
        logFact.setApplication(event.getReplyTo());
        logFact.setTenant(event.getTenant());
        logFact.setSubject(event.getSubject());
        logFact.setSession(String.valueOf(event.getSessionId()));
        logFact.setGroup(topic);
        logFact.setElement(event.getMessageId() != null ? event.getMessageId().toString() : null);
        logFact.setElementName(event.getTag());
        logFact.setValue(event.getPayload());
        if (event.getCreatedAt() != null) {
            logFact.setCreatedAt(event.getCreatedAt());
        }
        if (event.getCustomProperties() != null) {
            logFact.setOrganization(event.getCustomProperty(EventCustomProperties.ORGANIZATION));
            logFact.setFactType(event.getCustomProperty(EventCustomProperties.FACT_TYPE));
//            if (event.getCustomProperty(EventCustomProperties.ISSUER) != null) {
//                logFact.setCreatedBy(event.getCustomProperty(EventCustomProperties.ISSUER));
//            }

            final List<CustomProperty> customProperties = new ArrayList<>();
            for (final Map.Entry<String, String> entry : event.getCustomProperties().entrySet()) {
                customProperties.add(new CustomProperty(logFact, entry.getKey(), entry.getValue()));
            }
            logFact.setCustomProperties(customProperties);
        }
        EventsLogger.debug(this.getClass(), """
                        Event properties are:
                        \tCreatedBy: {}
                        \tReplyTo: {}
                        \tTenant: {}
                        \tSubject: {}
                        \tEntityType: {}
                        \tSessionId: {}
                        \tMessageId: {}
                        \tTag: {}
                        \tCreatedAt: {}
                        \tCustomProperties: {}
                        """,
                event.getCreatedBy(), event.getReplyTo(), event.getTenant(), event.getSubject(), event.getEntityType(), event.getSessionId(),
                event.getMessageId(), event.getTag(), event.getCreatedAt(), event.getCustomProperties());
        return logFact;
    }
}
