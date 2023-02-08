package com.biit.factmanager.client.fact;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class JsonValueDeserializer extends StdDeserializer<String> {

    protected JsonValueDeserializer() {
        super(String.class);
    }

    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        final String value = p.getValueAsString();
//        if (value.startsWith("{")) {
//            return value;
//        }
//        return "{" + value + "}";
        return value;
    }
}
