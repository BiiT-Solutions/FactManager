package com.biit.factmanager.dto;

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
        //jsonGenerator.writeString("{" + value + "}");
        jsonGenerator.writeString(value);
    }
}
