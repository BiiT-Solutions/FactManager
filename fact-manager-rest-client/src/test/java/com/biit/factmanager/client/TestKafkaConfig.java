package com.biit.factmanager.client;

import com.biit.kafka.config.KafkaConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.Map;

@Configuration
@Primary
public class TestKafkaConfig extends KafkaConfig {

    public Map<String, Object> getConsumerProperties() {
        final Map<String, Object> props = super.getConsumerProperties();
        //To ensure to avoid to read events from other applications.
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.biit.factmanager.*");
        return props;
    }
}
