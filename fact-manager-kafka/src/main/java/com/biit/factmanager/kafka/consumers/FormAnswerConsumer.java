package com.biit.factmanager.kafka.consumers;

import com.biit.factmanager.core.providers.FormrunnerFactProvider;
import com.biit.factmanager.logger.FactManagerLogger;
import com.biit.factmanager.persistence.configuration.FactManagerConfigurationReader;
import com.biit.factmanager.persistence.entities.FormrunnerFact;
import com.biit.kafkaclient.KafkaConsumerClient;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;

@Service
public class FormAnswerConsumer extends KafkaConsumerClient {

    @Autowired
    private FormrunnerFactProvider factProvider;

    private static final String TOPIC_NAME = "FormAnswer";
    // private static final List<Fact> CLASS_TYPE = new ArrayList<>();

    public FormAnswerConsumer() {
        super(FormrunnerFact.class);
        new Thread(() -> { // Start async and capture errors in the case there is no kafka running
            try {
                // Uncomment if you want to override standard kafka properties
                // setProperties(getPropertiesHardcoded());
                FactManagerLogger.debug(this.getClass().getName(), "Starting FormAnswersConsumer");
                startConsumer(Collections.singleton(TOPIC_NAME), getConsumer());
                FactManagerLogger.debug(this.getClass().getName(), "Started FormAnswersConsumer");
            } catch (Exception e) {
                FactManagerLogger.errorMessage(this.getClass().getName(), "Error starting the FormAnswerConsumer");
                FactManagerLogger.errorMessage(this.getClass().getName(), e.getMessage());
            }
        }).start();
    }

    private Consumer getConsumer() {
        return (x) -> {
            FactManagerLogger.debug(this.getClass().getName(), "FormAnswer event to save " + x.toString());
            final FormrunnerFact savedFact = factProvider.add((FormrunnerFact) x);
            FactManagerLogger.debug(this.getClass().getName(), "Saved fact " + savedFact.toString());
        };
    }

    public Properties getPropertiesHardcoded() {
        // TODO (oferrando): remove
        final Properties result = new Properties();
        result.setProperty(ConsumerConfig.CLIENT_ID_CONFIG, "ID" + Math.abs(new SecureRandom().nextInt(Integer.MAX_VALUE)));
        //result.setProperty(ConsumerConfig.CLIENT_ID_CONFIG, "ID" + 28L);
        result.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, FactManagerConfigurationReader.getInstance().getKeyDeserializerClassConfig());
        result.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, FactManagerConfigurationReader.getInstance().getValueDeserializerClassConfig());
        result.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, FactManagerConfigurationReader.getInstance().getBootstrapServersConfig());
        result.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, FactManagerConfigurationReader.getInstance().getKeySerializerClassConfig());
        result.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, FactManagerConfigurationReader.getInstance().getValueSerializerClassConfig());
        return result;
    }


    private Consumer getConsumer2() {
        return (x) -> {
            FactManagerLogger.debug(this.getClass().getName(), "Event consumed1 " + x.toString());
            final List<FormrunnerFact> facts = (ArrayList<FormrunnerFact>) x;
            FactManagerLogger.debug(this.getClass().getName(), "Event consumed2 " + facts.toString());
            FactManagerLogger.debug(this.getClass().getName(), "Event consumed3 " + x.toString());
            /*while (facts.iterator().hasNext()){
                FactManagerLogger.debug(this.getClass().getName(), "fact sto save " + facts.iterator().next().toString());
            }*/
            /*for (final Object fact: facts){
                FactManagerLogger.debug(this.getClass().getName(), "fact to save " + fact.toString());

                final Fact castedFact;
                try {
                    castedFact = JacksonSerializer.getDefaultSerializer().readValue(fact.toString(), Fact.class);
                    FactManagerLogger.debug(this.getClass().getName(), "fact to save " + (castedFact instanceof  Fact));
                    FactManagerLogger.debug(this.getClass().getName(), "fact to save " + castedFact.toString());
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }*/
            facts.forEach(fact -> {
                FactManagerLogger.debug(this.getClass().getName(), "fact to save " + fact.toString());
                final FormrunnerFact savedFact = factProvider.add((FormrunnerFact) fact);
                FactManagerLogger.debug(this.getClass().getName(), "Saved fact " + savedFact.toString());
            });

        };
    }

}
