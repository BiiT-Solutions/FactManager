package com.biit.factmanager.kafka;

import com.biit.factmanager.logger.FactManagerLogger;
import com.biit.factmanager.persistence.entities.Fact;
import com.biit.factmanager.persistence.entities.FormRunnerFact;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FactDeserializer implements Deserializer<Fact<?>> {
    public static final LocalDateTimeDeserializer LOCAL_DATETIME_SERIALIZER = new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(FactSerializer.DATETIME_FORMAT));

    private ObjectMapper objectMapper;

    private ObjectMapper getObjectMapper() {
        if (objectMapper == null) {
            final JavaTimeModule module = new JavaTimeModule();
            module.addDeserializer(LocalDateTime.class, LOCAL_DATETIME_SERIALIZER);
            objectMapper = Jackson2ObjectMapperBuilder.json()
                    .modules(module)
                    .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                    .build();
        }
        return objectMapper;
    }

    @Override
    public Fact<?> deserialize(String topic, byte[] bytes) {
        try {
            return getObjectMapper().readValue(new String(bytes, StandardCharsets.UTF_8), FormRunnerFact.class);
        } catch (IllegalArgumentException | JsonProcessingException e) {
            FactManagerLogger.debug(this.getClass(), "Not a Form Runner Fact.");
        }
        return getObjectMapper().convertValue(bytes, Fact.class);
    }
}
