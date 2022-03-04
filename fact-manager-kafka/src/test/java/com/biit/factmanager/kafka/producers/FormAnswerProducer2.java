package com.biit.factmanager.kafka.producers;

import com.biit.factmanager.core.providers.FactProvider;
import com.biit.factmanager.kafka.KafkaConfig;
import com.biit.factmanager.persistence.entities.FormRunnerFact;
import com.biit.factmanager.persistence.entities.values.FormRunnerValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

@EnableKafka
@Configuration
public class FormAnswerProducer2 extends FactProducer<FormRunnerValue, FormRunnerFact> {

    private final KafkaConfig kafkaConfig;
    private KafkaTemplate<String, FormRunnerFact> factKafkaFormAnswerTemplate;

    @Autowired
    public FormAnswerProducer2(FactProvider<FormRunnerFact> factProvider, KafkaConfig kafkaConfig) {
        super(factProvider, kafkaConfig);
        this.kafkaConfig = kafkaConfig;
    }

    protected KafkaTemplate<String, FormRunnerFact> getFactKafkaTemplate() {
        if (factKafkaFormAnswerTemplate == null) {
            factKafkaFormAnswerTemplate = new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(kafkaConfig.getProperties()));
        }
        return factKafkaFormAnswerTemplate;
    }

}
