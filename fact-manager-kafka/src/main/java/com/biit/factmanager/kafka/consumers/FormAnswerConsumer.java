package com.biit.factmanager.kafka.consumers;

import com.biit.factmanager.core.providers.FactProvider;
import com.biit.factmanager.logger.FactManagerLogger;
import com.biit.factmanager.persistence.entities.FormRunnerFact;
import com.biit.factmanager.persistence.entities.values.FormRunnerValue;
import com.biit.kafkaclient.KafkaConsumerClient;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;

@Service
public class FormAnswerConsumer extends FactConsumer<FormRunnerValue, FormRunnerFact> {

    @Autowired
    public FormAnswerConsumer(FactProvider<FormRunnerFact> factProvider) {
        super(factProvider);
    }
}
