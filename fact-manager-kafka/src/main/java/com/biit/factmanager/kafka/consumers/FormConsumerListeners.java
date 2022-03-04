package com.biit.factmanager.kafka.consumers;

import com.biit.factmanager.core.providers.FactProvider;
import com.biit.factmanager.logger.FactManagerLogger;
import com.biit.factmanager.persistence.entities.Fact;
import com.biit.kafka.consumers.EventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;

@EnableKafka
@Configuration
public class FormConsumerListeners<V, T extends Fact<V>> extends EventListener<T> {
    private final FactProvider<T> factProvider;

    @Autowired
    public FormConsumerListeners(FactProvider<T> factProvider) {
        super();
        this.factProvider = factProvider;
    }

    @Override
    @KafkaListener(topics = "${kafka.topic}", clientIdPrefix = "firstListener", containerFactory = "factsKafkaListenerContainerFactory")
    public void eventsListener(T fact) {
        FactManagerLogger.debug(this.getClass().getName(), "Fact event to save " + fact.toString());
        final Fact<V> savedFact = factProvider.save((T) fact);
        FactManagerLogger.debug(this.getClass().getName(), "Saved fact " + savedFact.toString());
        getListeners().forEach(factReceivedListener -> factReceivedListener.received(fact));
    }
}
