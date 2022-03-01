package com.biit.factmanager.kafka.consumers;

import com.biit.factmanager.core.providers.FactProvider;
import com.biit.factmanager.logger.FactManagerLogger;
import com.biit.factmanager.persistence.entities.Fact;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.HashSet;
import java.util.Set;

@EnableKafka
@Configuration
public class FormConsumerListeners2<V, T extends Fact<V>> {

    private Set<FactReceivedListener> listeners;

    private final FactProvider<T> factProvider;

    public interface FactReceivedListener {
        void received(Fact<?> fact);
    }

    @Autowired
    public FormConsumerListeners2(FactProvider<T> factProvider) {
        this.listeners = new HashSet<>();
        this.factProvider = factProvider;
    }


    public void addListener(FactReceivedListener listener) {
        listeners.add(listener);
    }


    @KafkaListener(topics = "${kafka.topic}", containerFactory = "factsKafkaListenerContainerFactory")
    public void factsListener(T fact) {
        FactManagerLogger.debug(this.getClass().getName(), "Fact event to save " + fact.toString());
        final Fact<V> savedFact = factProvider.save((T) fact);
        FactManagerLogger.debug(this.getClass().getName(), "Saved fact " + savedFact.toString());
        listeners.forEach(factReceivedListener -> factReceivedListener.received(fact));
    }
}
