package com.biit.factmanager.kafka.producers;

import com.biit.factmanager.core.providers.FactProvider;
import com.biit.factmanager.kafka.KafkaConfig;
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
                    System.out.println("Sent fact=[" + fact + "] with offset=[" + result.getRecordMetadata()
                            .offset() + "]");
                }
            }

            @Override
            public void onFailure(Throwable ex) {
                System.out.println("Unable to send fact=[" + fact + "] due to: " + ex.getMessage());
            }
        });
    }

    protected abstract KafkaTemplate<String, T> getFactKafkaTemplate();

}
