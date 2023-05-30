package com.biit.factmanager.kafka.producers;

import com.biit.kafka.config.KafkaConfig;
import com.biit.kafka.producers.TemplateEventProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FormAnswerProducer extends TemplateEventProducer<FormrunnerQuestionFactEvent> {

    @Autowired
    public FormAnswerProducer(KafkaConfig kafkaConfig) {
        super(kafkaConfig);
    }

}
