package com.biit.factmanager.kafka.consumers;

import com.biit.factmanager.kafka.producers.FormrunnerQuestionFactEvent;
import com.biit.kafka.config.KafkaConfig;
import com.biit.kafka.consumers.TemplateEventConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@Configuration
public class FormAnswerConsumer2 extends TemplateEventConsumer<FormrunnerQuestionFactEvent> {

    @Autowired
    public FormAnswerConsumer2(KafkaConfig kafkaConfig) {
        super(FormrunnerQuestionFactEvent.class, kafkaConfig);
    }
}
