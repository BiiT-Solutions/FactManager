package com.biit.factmanager.persistence.entities;

import com.biit.factmanager.logger.FactManagerLogger;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.AttributeConverter;
import java.io.IOException;
import java.util.Map;

public class HashMapConverter<Value> implements AttributeConverter<Map<String, String>, String> {
    @Override
    public String convertToDatabaseColumn(Map<String, String> attribute) {
        String attributeAsJson = null;
        try {
            attributeAsJson = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL).writeValueAsString(attribute);
        } catch (final JsonProcessingException e) {
            FactManagerLogger.errorMessage(this.getClass().getName(), e);
        }

        return attributeAsJson;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, String> convertToEntityAttribute(String dbData) {
        Map<String, String> attribute = null;
        try {
            attribute = (Map<String, String>) new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL).readValue(dbData, Map.class);
        } catch (final IOException e) {
            FactManagerLogger.errorMessage(this.getClass().getName(), e);
        }

        return attribute;
    }
}
