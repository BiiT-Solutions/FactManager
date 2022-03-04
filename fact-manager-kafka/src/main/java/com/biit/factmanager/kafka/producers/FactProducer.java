package com.biit.factmanager.kafka.producers;

import com.biit.factmanager.core.providers.FactProvider;
import com.biit.factmanager.kafka.KafkaConfig;
import com.biit.factmanager.logger.FactManagerLogger;
import com.biit.factmanager.persistence.entities.Fact;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

public abstract class FactProducer<V, T extends Fact<V>> {

    private final FactProvider<T> factProvider;

    private final KafkaConfig kafkaConfig;

    @Value("${kafka.topic:}")
    private String kafkaTopic;

    public FactProducer(FactProvider<T> factProvider, KafkaConfig kafkaConfig) {
        this.factProvider = factProvider;
        this.kafkaConfig = kafkaConfig;
    }


    public void sendFact(T fact) {
        final ListenableFuture<SendResult<String, T>> future = getFactKafkaTemplate().send(kafkaTopic, fact);
        future.addCallback(new ListenableFutureCallback<SendResult<String, T>>() {

            @Override
            public void onSuccess(SendResult<String, T> result) {
                if (result != null) {
                    FactManagerLogger.debug(this.getClass(), "Sent fact '{}' with offset '{}'.", fact,
                            result.getRecordMetadata().offset());
                } else {
                    FactManagerLogger.warning(this.getClass(), "Sent fact '{}' with no result.", fact);
                }
            }

            @Override
            public void onFailure(Throwable ex) {
                FactManagerLogger.severe(this.getClass(), "\"Unable to send fact '{}' due to '{}'", fact,
                        ex.getMessage());
            }
        });
    }

    protected abstract KafkaTemplate<String, T> getFactKafkaTemplate();

}
