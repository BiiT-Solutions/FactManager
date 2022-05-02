package com.biit.factmanager.kafka.producers;

import com.biit.factmanager.persistence.entities.FormrunnerQuestionFact;
import com.biit.kafka.config.KafkaConfig;
import com.biit.kafka.producers.EventProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@Configuration
public class FormAnswerProducer extends EventProducer<FormrunnerQuestionFact> {

    @Autowired
    public FormAnswerProducer(KafkaConfig kafkaConfig) {
        super(kafkaConfig);
    }

}
