package com.biit.factmanager.kafka.consumers;

import com.biit.factmanager.core.providers.FactProvider;
import com.biit.factmanager.kafka.KafkaConfig;
import com.biit.factmanager.logger.FactManagerLogger;
import com.biit.factmanager.persistence.entities.Fact;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

public abstract class FactConsumer<V, T extends Fact<V>> {

    private final FactProvider<T> factProvider;

    private final KafkaConfig kafkaConfig;

    public FactConsumer(FactProvider<T> factProvider, KafkaConfig kafkaConfig) {
        this.factProvider = factProvider;
        this.kafkaConfig = kafkaConfig;
    }


    private ConcurrentKafkaListenerContainerFactory<String, T> kafkaListenerContainerFactory() {
        final ConcurrentKafkaListenerContainerFactory<String, T> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(kafkaConfig.getProperties()));
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, T> factsKafkaListenerContainerFactory() {
        FactManagerLogger.debug(this.getClass().getName(), "Starting FactConsumer");
        try {
            return kafkaListenerContainerFactory();
        } catch (Exception e) {
            FactManagerLogger.errorMessage(this.getClass().getName(), "Error starting the FactConsumer");
            FactManagerLogger.errorMessage(this.getClass().getName(), e.getMessage());
        } finally {
            FactManagerLogger.debug(this.getClass().getName(), "Started FactConsumer");
        }
        return null;
    }
}
