package com.biit.factmanager.kafka.consumers;

import com.biit.factmanager.core.providers.FactProvider;
import com.biit.factmanager.kafka.models.StringEvent;
import com.biit.factmanager.logger.FactManagerLogger;
import com.biit.factmanager.persistence.entities.StringFact;
import com.biit.factmanager.persistence.entities.values.StringValue;
import com.biit.kafka.consumers.EventListener;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;

@EnableKafka
@Configuration
public class StringEventConsumerListener extends EventListener<StringEvent> {
    private static final String ORGANIZATION_NODE = "organization";
    private static final String PROCESS_NODE = "process";
    private final FactProvider<StringFact> factProvider;
    private final ObjectMapper objectMapper;

    @Autowired
    public StringEventConsumerListener(FactProvider<StringFact> factProvider, ObjectMapper objectMapper) {
        super();
        this.factProvider = factProvider;
        this.objectMapper = objectMapper;
    }

    @Override
    @KafkaListener(topics = "*", groupId = "${spring.kafka.group.id}", clientIdPrefix = "firstListener",
            containerFactory = "templateEventListenerContainerFactory")
    public void eventsListener(StringEvent event) {
        super.eventsListener(event);
        final StringFact savedFact = factProvider.save(convert(event));
        FactManagerLogger.debug(this.getClass().getName(), "Saved fact " + savedFact.toString());
    }

    private StringFact convert(StringEvent event) {
        final StringFact stringFact = new StringFact();
        stringFact.setIssuer(event.getIssuer());
        stringFact.setTenant(event.getTenant());
        stringFact.setTag(event.getSubject());
        //stringFact.setGroup(event.getSessionId());
        stringFact.setEntity(new StringValue(event.getEntity()));
        if (event.getCreateAt() != null) {
            stringFact.setCreatedAt(event.getCreateAt());
        }
        try {
            final JsonNode content = new ObjectMapper().readTree(event.getPayload());
            stringFact.setOrganization(content.get(ORGANIZATION_NODE) != null ? content.get(ORGANIZATION_NODE).asText() : null);
            stringFact.setOrganization(content.get(PROCESS_NODE) != null ? content.get(PROCESS_NODE).asText() : null);
        } catch (JsonProcessingException e) {
            FactManagerLogger.errorMessage(this.getClass(), e);
        }
        return stringFact;
    }

}
