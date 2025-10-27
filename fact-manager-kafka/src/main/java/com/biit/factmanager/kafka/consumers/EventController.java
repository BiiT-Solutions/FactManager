package com.biit.factmanager.kafka.consumers;

/*-
 * #%L
 * FactManager (Kafka)
 * %%
 * Copyright (C) 2020 - 2025 BiiT Sourcing Solutions S.L.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.biit.factmanager.core.providers.CustomPropertyProvider;
import com.biit.factmanager.core.providers.FactProvider;
import com.biit.factmanager.core.providers.exceptions.FactNotFoundException;
import com.biit.factmanager.kafka.senders.EventSender;
import com.biit.factmanager.logger.FactManagerLogger;
import com.biit.factmanager.persistence.entities.CustomProperty;
import com.biit.factmanager.persistence.entities.LogFact;
import com.biit.kafka.consumers.EventListener;
import com.biit.kafka.events.Event;
import com.biit.kafka.events.EventCustomProperties;
import com.biit.kafka.logger.EventsLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Controller;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TimeZone;
import java.util.UUID;


@Controller
@ConditionalOnExpression("${spring.kafka.enabled:false}")
public class EventController {
    private static final String CONTROL_TOPIC_1 = "_connect_configs";
    private static final String CONTROL_TOPIC_2 = "_connect_offset";
    private static final String CONTROL_TOPIC_3 = "_connect_status";

    private final FactProvider<LogFact> factProvider;
    private final CustomPropertyProvider<LogFact> customPropertyProvider;
    private final EventSender eventSender;


    @Autowired(required = false)
    public EventController(EventListener eventListener,
                           EventConsumerListener eventConsumerListener,
                           FactProvider<LogFact> factProvider, CustomPropertyProvider<LogFact> customPropertyProvider, EventSender eventSender) {

        this.factProvider = factProvider;
        this.customPropertyProvider = customPropertyProvider;
        this.eventSender = eventSender;

        //Listen to topic
        if (eventListener != null) {
            eventListener.addListener((event, offset, groupId, key, partition, topic, timeStamp) -> {
                if (!Objects.equals(CONTROL_TOPIC_1, topic) && !Objects.equals(CONTROL_TOPIC_2, topic) && !Objects.equals(CONTROL_TOPIC_3, topic)) {
                    EventsLogger.debug(this.getClass(), "Received event '{}' on topic '{}', key '{}', partition '{}' at '{}'",
                            event, topic, groupId, key, partition, LocalDateTime.ofInstant(Instant.ofEpochMilli(timeStamp),
                                    TimeZone.getDefault().toZoneId()));
                }
            });
        }

        //Listens to all events on Kafka Streams.
        if (eventConsumerListener != null) {
            eventConsumerListener.addListener((event, offset, groupId, key, partition, topic, timeStamp) -> {
                if (!Objects.equals(CONTROL_TOPIC_1, topic) && !Objects.equals(CONTROL_TOPIC_2, topic) && !Objects.equals(CONTROL_TOPIC_3, topic)) {
                    EventsLogger.debug(this.getClass(), "Received event '{}' on topic '{}', key '{}', partition '{}' at '{}'",
                            event, topic, groupId, key, partition, LocalDateTime.ofInstant(Instant.ofEpochMilli(timeStamp),
                                    TimeZone.getDefault().toZoneId()));
                    //Only Labstation events.
                    if (event != null) {
                        final LogFact savedFact = factProvider.save(convert(event, topic));
                        EventsLogger.debug(this.getClass().getName(), "Saved fact " + savedFact.toString());
                    } else {
                        FactManagerLogger.warning(this.getClass(), "Receiving null event! Fact cannot be saved.");
                    }
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

        if (event.getSessionId() != null) {
            logFact.setSession(String.valueOf(event.getSessionId()));
        }
        logFact.setGroup(topic);
        if (event.getMessageId() != null) {
            logFact.setElement(String.valueOf(event.getMessageId()));
        }
        logFact.setElementName(event.getTag());
        logFact.setValue(event.getPayload());
        logFact.setOrganization(event.getOrganization());
        logFact.setUnit(event.getUnit());
        if (event.getCreatedAt() != null) {
            logFact.setCreatedAt(event.getCreatedAt());
        }
        if (event.getCustomProperties() != null) {
            if (logFact.getOrganization() == null) {
                //Compatibility with old versions.
                logFact.setOrganization(event.getCustomProperty(EventCustomProperties.ORGANIZATION));
            }
            if (event.getCustomProperty(EventCustomProperties.FACT_TYPE) != null) {
                logFact.setFactType(event.getCustomProperty(EventCustomProperties.FACT_TYPE));
            } else if (event.getEntityType() != null && event.getEntityType().contains(".")) {
                final String[] parts = event.getEntityType().split("\\.");
                logFact.setFactType(parts[parts.length - 1]);
            }

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


    public void resendFact(Long id) {
        final Optional<LogFact> fact = factProvider.get(id);
        if (fact.isPresent()) {
            final Event event = convert(fact.get());
            eventSender.sendEvent(event, fact.get().getGroup());
        } else {
            throw new FactNotFoundException(this.getClass(), "No fact found with id '" + id + "' found.");
        }
    }


    private Event convert(LogFact logFact) {
        if (logFact == null) {
            FactManagerLogger.warning(this.getClass(), "Receiving null fact!");
            return null;
        }
        final Event event = new Event();
        event.setCreatedBy(logFact.getCreatedBy());
        event.setReplyTo(logFact.getApplication());
        event.setTenant(logFact.getTenant());
        event.setSubject(logFact.getSubject());

        if (logFact.getSession() != null) {
            event.setSessionId(UUID.fromString(logFact.getSession()));
        }

        if (logFact.getElement() != null) {
            event.setMessageId(UUID.fromString(logFact.getElement()));
        }

        event.setTag(logFact.getElementName());
        event.setPayload(logFact.getValue());
        event.setOrganization(logFact.getOrganization());
        event.setUnit(logFact.getUnit());
        if (logFact.getCreatedAt() != null) {
            event.setCreatedAt(logFact.getCreatedAt());
        }
        event.setCustomProperties(new HashMap<>());
        if (logFact.getFactType() != null) {
            event.setCustomProperty(EventCustomProperties.FACT_TYPE, logFact.getFactType());
        }

        final List<CustomProperty> properties = customPropertyProvider.findByFact(logFact);

        for (final CustomProperty customProperty : properties) {
            event.setCustomProperty(customProperty.getKey(), customProperty.getValue());
        }

        event.setEntityType(logFact.getFactType());

        return event;
    }
}
