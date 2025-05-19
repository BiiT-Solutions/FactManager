package com.biit.factmanager.kafka.senders;

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
