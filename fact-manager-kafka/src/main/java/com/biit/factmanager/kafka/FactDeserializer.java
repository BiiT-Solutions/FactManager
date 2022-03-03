package com.biit.factmanager.kafka;

import com.biit.factmanager.persistence.entities.Fact;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.apache.kafka.common.serialization.Deserializer;

import java.time.format.DateTimeFormatter;

public class FactDeserializer implements Deserializer<Fact<?>> {
    public static final String DATETIME_FORMAT = "dd-MM-yyyy HH:mm";
    public static LocalDateTimeSerializer LOCAL_DATETIME_SERIALIZER = new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DATETIME_FORMAT));

    private ObjectMapper objectMapper;

    private ObjectMapper getObjectMapper() {
        if (objectMapper == null) {
            JavaTimeModule module = new JavaTimeModule();
            module.addSerializer(LOCAL_DATETIME_SERIALIZER);
            objectMapper = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL).registerModule(module);
        }
        return objectMapper;
    }

    @Override
    public Fact<?> deserialize(String s, byte[] bytes) {
        return getObjectMapper().convertValue(bytes, Fact.class);
    }
}
