package com.biit.factmanager.client.fact;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class JsonValueSerializer extends StdSerializer<String> {

    protected JsonValueSerializer() {
        super(String.class);
    }

    @Override
    public void serialize(String value, JsonGenerator jsonGenerator, SerializerProvider provider) throws IOException {
        if (value != null && !value.isEmpty()) {
            jsonGenerator.writeString(value.substring(1).substring(0, value.length() - 2));
        }
    }
}
