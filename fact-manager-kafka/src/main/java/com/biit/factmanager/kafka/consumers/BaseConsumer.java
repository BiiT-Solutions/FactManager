package com.biit.factmanager.kafka.consumers;

import com.biit.factmanager.logger.FactManagerLogger;
import com.biit.factmanager.persistence.configuration.FactManagerConfigurationReader;
import com.biit.kafkaclient.KafkaConsumerClient;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.Properties;
import java.util.function.Consumer;

public class BaseConsumer extends KafkaConsumerClient {


    public BaseConsumer(String topic, Class deserializeClass, Consumer consumer){
        super(deserializeClass);
        setProperties(getProperties());
        new Thread(() -> { // Start async and capture errors in the case there is no kafka running
            try {
                // TODO (oferrando): remove hardcoded properties
                FactManagerLogger.debug(this.getClass().getName(), "Starting " + deserializeClass.getName());
                startConsumer(Collections.singleton(topic), consumer);
                FactManagerLogger.debug(this.getClass().getName(), "Started " + deserializeClass.getName());
            } catch (Exception e) {
                FactManagerLogger.errorMessage(this.getClass().getName(), "Error starting the " + deserializeClass.getName());
                FactManagerLogger.errorMessage(this.getClass().getName(), e.getMessage());
            }
        }).start();
    }


    public Properties getProperties() {
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


}
