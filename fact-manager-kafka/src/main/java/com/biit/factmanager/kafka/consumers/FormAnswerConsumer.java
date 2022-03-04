package com.biit.factmanager.kafka.consumers;

import com.biit.factmanager.persistence.entities.FormRunnerFact;
import com.biit.kafka.KafkaConfig;
import com.biit.kafka.consumers.EventConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@Configuration
public class FormAnswerConsumer extends EventConsumer<FormRunnerFact> {

    @Autowired
    public FormAnswerConsumer(KafkaConfig kafkaConfig) {
        super(kafkaConfig);
    }
}
