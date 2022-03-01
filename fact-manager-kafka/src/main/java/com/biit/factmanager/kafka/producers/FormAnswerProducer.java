package com.biit.factmanager.kafka.producers;

import com.biit.factmanager.core.providers.FactProvider;
import com.biit.factmanager.kafka.KafkaConfig;
import com.biit.factmanager.persistence.entities.FormRunnerFact;
import com.biit.factmanager.persistence.entities.values.FormRunnerValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@Configuration
public class FormAnswerProducer extends FactProducer<FormRunnerValue, FormRunnerFact> {

    @Autowired
    public FormAnswerProducer(FactProvider<FormRunnerFact> factProvider, KafkaConfig kafkaConfig) {
        super(factProvider, kafkaConfig);
    }

}
