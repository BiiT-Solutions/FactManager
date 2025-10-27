package com.biit.factmanager.kafka.senders;

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

import com.biit.kafka.events.Event;
import com.biit.kafka.events.KafkaEventTemplate;
import com.biit.kafka.logger.EventsLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnExpression("${spring.kafka.enabled:false}")
public class EventSender {

    private final KafkaEventTemplate kafkaTemplate;

    private EventSender() {
        this.kafkaTemplate = null;
    }

    @Autowired(required = false)
    public EventSender(KafkaEventTemplate kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEvent(Event event, String topic) {
        EventsLogger.debug(this.getClass().getName(), "Preparing for sending events...");
        if (kafkaTemplate != null && topic != null && !topic.isEmpty()) {
            //Send the complete form as an event.
            kafkaTemplate.send(topic, event);
            EventsLogger.debug(this.getClass().getName(), "Event send to topic '{}'!", topic);
        } else {
            EventsLogger.warning(this.getClass().getName(), "Topic invalid!");
        }
    }
}
