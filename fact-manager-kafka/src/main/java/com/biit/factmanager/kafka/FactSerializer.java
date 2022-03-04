package com.biit.factmanager.kafka;

import com.biit.factmanager.logger.FactManagerLogger;
import com.biit.factmanager.persistence.entities.Fact;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.apache.kafka.common.serialization.Serializer;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;

public class FactSerializer implements Serializer<Fact<?>> {
    public static final String DATETIME_FORMAT = "dd-MM-yyyy HH:mm:ss.SSS";
    public static final LocalDateTimeSerializer LOCAL_DATETIME_SERIALIZER = new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DATETIME_FORMAT));

    private ObjectMapper objectMapper;

    private ObjectMapper getObjectMapper() {
        if (objectMapper == null) {
            final JavaTimeModule module = new JavaTimeModule();
            module.addSerializer(LOCAL_DATETIME_SERIALIZER);
            objectMapper = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL).registerModule(module);
        }
        return objectMapper;
    }

    @Override
    public byte[] serialize(String s, Fact<?> fact) {
        try {
            return getObjectMapper().writeValueAsString(fact).getBytes(StandardCharsets.UTF_8);
        } catch (JsonProcessingException e) {
            FactManagerLogger.errorMessage(this.getClass(), e);
        }
        return new byte[0];
    }
}
