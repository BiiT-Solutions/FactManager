package com.biit.factmanager.kafka.producers;

import com.biit.factmanager.persistence.entities.FormrunnerQuestionFact;
import com.biit.kafka.config.KafkaConfig;
import com.biit.kafka.producers.TemplateEventProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@Configuration
public class FormAnswerProducer2 extends TemplateEventProducer<FormrunnerQuestionFactEvent> {

    @Autowired
    public FormAnswerProducer2(KafkaConfig kafkaConfig) {
        super(kafkaConfig);
    }

}
