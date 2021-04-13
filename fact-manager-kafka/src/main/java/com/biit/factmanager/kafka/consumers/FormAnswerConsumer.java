package com.biit.factmanager.kafka.consumers;

import com.biit.factmanager.core.providers.FactProvider;
import com.biit.factmanager.logger.FactManagerLogger;
import com.biit.factmanager.persistence.entities.Fact;
import com.biit.kafkaclient.KafkaConsumerClient;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Consumer;

@Service
public class FormAnswerConsumer extends KafkaConsumerClient {

    @Autowired
    private FactProvider factProvider;

    private static final String TOPIC_NAME = "FormAnswer";

    public FormAnswerConsumer(){
        super(Fact.class);
        // TODO (oferrando): remove hardcoded properties
        setProperties(getPropertiesHardcoded());
        FactManagerLogger.debug(this.getClass().getName(), "Starting FormAnswersConsumer");
        startConsumer(Collections.singleton(TOPIC_NAME), getConsumer());
        FactManagerLogger.debug(this.getClass().getName(), "Started FormAnswersConsumer");
    }

    private Consumer getConsumer(){
        return (x) -> {
            FactManagerLogger.debug(this.getClass().getName(), "fact to save " + x.toString());
            final Fact savedFact = factProvider.add((Fact) x);
            FactManagerLogger.debug(this.getClass().getName(), "Saved fact " + savedFact.toString());
        };
    }

    public Properties getPropertiesHardcoded() {
        // TODO (oferrando): remove
        final Properties result = new Properties();
        // result.setProperty(ConsumerConfig.CLIENT_ID_CONFIG, "ID" + Math.abs(new Random().nextLong()));
        result.setProperty(ConsumerConfig.CLIENT_ID_CONFIG, "ID" + 28L);
        result.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        result.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        result.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
        result.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        result.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        return result;
    }


    private Consumer getConsumer2(){
        return (x) -> {
            final List<Fact> facts = (ArrayList<Fact>) x;
            FactManagerLogger.debug(this.getClass().getName(), "Event consumed " + facts.toString());
            FactManagerLogger.debug(this.getClass().getName(), "Event consumed " + x.toString());
            while (facts.iterator().hasNext()){
                FactManagerLogger.debug(this.getClass().getName(), "fact sto save " + facts.iterator().next().toString());
            }
            for (final Fact fact: facts){
                FactManagerLogger.debug(this.getClass().getName(), "fact to save " + fact.toString());
            }
            facts.forEach(fact -> {
                FactManagerLogger.debug(this.getClass().getName(), "fact to save " + fact.toString());
                final Fact savedFact = factProvider.add((Fact) fact);
                FactManagerLogger.debug(this.getClass().getName(), "Saved fact " + savedFact.toString());
            });

        };
    }

}
