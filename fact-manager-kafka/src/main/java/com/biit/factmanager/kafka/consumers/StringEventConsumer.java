package com.biit.factmanager.kafka.consumers;

import com.biit.factmanager.kafka.models.StringEvent;
import com.biit.kafka.config.KafkaConfig;
import com.biit.kafka.consumers.TemplateEventConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@Configuration
public class StringEventConsumer extends TemplateEventConsumer<StringEvent> {

    @Autowired
    public StringEventConsumer(KafkaConfig kafkaConfig) {
        super(StringEvent.class, kafkaConfig);
    }
}
