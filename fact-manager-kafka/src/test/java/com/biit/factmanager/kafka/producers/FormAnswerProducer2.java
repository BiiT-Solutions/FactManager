package com.biit.factmanager.kafka.producers;

import com.biit.factmanager.persistence.entities.FormRunnerFact;
import com.biit.kafka.KafkaConfig;
import com.biit.kafka.producers.EventProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@Configuration
public class FormAnswerProducer2 extends EventProducer<FormRunnerFact> {

    @Autowired
    public FormAnswerProducer2(KafkaConfig kafkaConfig) {
        super(kafkaConfig);
    }

}
