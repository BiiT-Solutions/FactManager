package com.biit.factmanager.kafka.consumers;

import com.biit.factmanager.core.providers.FactProvider;
import com.biit.factmanager.logger.FactManagerLogger;
import com.biit.factmanager.persistence.entities.Fact;
import com.biit.kafkaclient.KafkaConsumerClient;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.Properties;
import java.util.function.Consumer;

public abstract class FactConsumer<V, T extends Fact<V>> extends KafkaConsumerClient<T> {

    private final FactProvider<T> factProvider;

    @Value("${kafka.topic:}")
    private String kafkaTopic;

    @Value("${kafka.bootstrap.servers:}")
    private String kafkaBootstrapServers;

    @Value("${kafka.client.id:}")
    private String kafkaClientId;

    @Value("${kafka.group.id:}")
    private String kafkaGroupId;

    @Value("${kafka.key.serializer:}")
    private String kafkaKeySerializer;

    @Value("${kafka.value.serializer:}")
    private String kafkaValueSerializer;

    @Value("${kafka.key.deserializer:}")
    private String kafkaKeyDeserializer;

    @Value("${kafka.value.deserializer:}")
    private String kafkaValueDeserializer;

    public FactConsumer(Class<T> clazz, FactProvider<T> factProvider) {
        super(clazz);
        this.factProvider = factProvider;
    }

    public void start() {
        new Thread(() -> { // Start async and capture errors in the case there is no kafka running
            try {
                setProperties(getPropertiesHardcoded());
                FactManagerLogger.debug(this.getClass().getName(), "Starting FormAnswersConsumer");
                startConsumer(Collections.singleton(kafkaTopic), getConsumer());
                FactManagerLogger.debug(this.getClass().getName(), "Started FormAnswersConsumer");
            } catch (Exception e) {
                FactManagerLogger.errorMessage(this.getClass().getName(), "Error starting the FormAnswerConsumer");
                FactManagerLogger.errorMessage(this.getClass().getName(), e.getMessage());
            }
        }).start();
    }

    protected Properties getPropertiesHardcoded() {
        final Properties result = new Properties();
        if (kafkaClientId != null) {
            result.setProperty(ConsumerConfig.CLIENT_ID_CONFIG, "ID" + kafkaClientId);
        } else {
            result.setProperty(ConsumerConfig.CLIENT_ID_CONFIG, "ID" + Math.abs(new SecureRandom().nextInt(Integer.MAX_VALUE)));
        }
        if (kafkaKeyDeserializer != null) {
            result.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, kafkaKeyDeserializer);
        }
        if (kafkaValueDeserializer != null) {
            result.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, kafkaValueDeserializer);
        }
        if (kafkaBootstrapServers != null) {
            result.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
        }
        if (kafkaKeySerializer != null) {
            result.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, kafkaKeySerializer);
        }
        if (kafkaValueSerializer != null) {
            result.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, kafkaValueSerializer);
        }
        return result;
    }

    protected Consumer<T> getConsumer() {
        return (x) -> {
            FactManagerLogger.debug(this.getClass().getName(), "FormAnswer event to save " + x.toString());
            final Fact<V> savedFact = factProvider.save((T) x);
            FactManagerLogger.debug(this.getClass().getName(), "Saved fact " + savedFact.toString());
        };
    }
}
