package com.biit.factmanager.test;

import com.biit.factmanager.core.providers.FactProvider;
import com.biit.factmanager.kafka.consumers.FormAnswerConsumer;
import com.biit.factmanager.persistence.entities.FormRunnerFact;
import com.biit.factmanager.persistence.entities.values.FormRunnerValue;
import com.biit.kafkaclient.IKafkaConsumerClient;
import com.biit.kafkaclient.KafkaConsumerClient;
import com.biit.kafkaclient.KafkaProducerClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@SpringBootTest
@Test(groups = {"kafkaEvents"})
public class KafkaTests extends AbstractTransactionalTestNGSpringContextTests {
    private static final String TOPIC_NAME = "facts";
    private static final int EVENTS_QUANTITY = 100;
    private static final String QUESTION = "Question? ";
    private static final String ANSWER = "Answer: ";

    @Autowired
    private FactProvider<FormRunnerFact> factProvider;

    private FormRunnerFact generateEvent(int value) {
        FormRunnerFact formRunnerFact = new FormRunnerFact();
        FormRunnerValue formRunnerValue = new FormRunnerValue();
        formRunnerValue.setQuestion(QUESTION + value);
        formRunnerValue.setAnswer(ANSWER + value);
        formRunnerFact.setEntity(formRunnerValue);
        return formRunnerFact;
    }

    public FormRunnerFact generateEvent(int value, LocalDateTime minTimestamp, LocalDateTime maxTimestamp) {
        FormRunnerFact formRunnerFact = generateEvent(value);

        //Create a random day.
        // create ZoneId
        ZoneOffset zone = ZoneOffset.of("Z");
        long randomSecond = ThreadLocalRandom.current().nextLong(minTimestamp.toEpochSecond(zone), maxTimestamp.toEpochSecond(zone));
        LocalDateTime randomDate = LocalDateTime.ofEpochSecond(randomSecond, 0, zone);

        formRunnerFact.setCreatedAt(randomDate);
        return formRunnerFact;
    }

    public synchronized void factTest() throws InterruptedException {
        FormAnswerConsumer kafkaClient = new FormAnswerConsumer(factProvider);
        Set<FormRunnerFact> consumerEvents = Collections.synchronizedSet(new HashSet<>(EVENTS_QUANTITY));
        Set<FormRunnerFact> producerEvents = new HashSet<>(EVENTS_QUANTITY);
        //Store received events into set.
        kafkaClient.startConsumer(Collections.singleton(TOPIC_NAME), consumerEvents::add);
        for (int i = 0; i < EVENTS_QUANTITY; i++) {
            FormRunnerFact generatedEvent = generateEvent(i);
            producerEvents.add(generatedEvent);
            KafkaProducerClient.send(TOPIC_NAME, generatedEvent);
        }
        wait(getWaitingTime());
        Assert.assertEquals(consumerEvents, producerEvents);
    }

    public synchronized void multipleProducerTest() throws InterruptedException {
        IKafkaConsumerClient<FormRunnerFact> kafkaClient = new FormAnswerConsumer(factProvider);
        Set<FormRunnerFact> consumerEvents = Collections.synchronizedSet(new HashSet<>(EVENTS_QUANTITY * 2));
        Set<FormRunnerFact> producerEvents = new HashSet<>(EVENTS_QUANTITY);
        Set<FormRunnerFact> producerEvents2 = new HashSet<>(EVENTS_QUANTITY);
        kafkaClient.startConsumer(Collections.singleton(TOPIC_NAME), consumerEvents::add);
        for (int i = 0; i < EVENTS_QUANTITY; i++) {
            FormRunnerFact generatedEvent = generateEvent(i);
            producerEvents.add(generatedEvent);
            KafkaProducerClient.send(TOPIC_NAME, generatedEvent);
            FormRunnerFact generatedEvent2 = generateEvent(i);
            producerEvents2.add(generatedEvent2);
            KafkaProducerClient.send(TOPIC_NAME, generatedEvent2);
        }
        producerEvents.addAll(producerEvents2);
        wait(getWaitingTime());
        Assert.assertEquals(consumerEvents, producerEvents);
    }

    public synchronized void multipleConsumerTest() throws InterruptedException {
        IKafkaConsumerClient<FormRunnerFact> kafkaClient = new FormAnswerConsumer(factProvider);
        Set<FormRunnerFact> consumerEvents = Collections.synchronizedSet(new HashSet<>(EVENTS_QUANTITY));
        IKafkaConsumerClient<FormRunnerFact> kafkaClient2 = new KafkaConsumerClient<>(FormRunnerFact.class);
        Set<FormRunnerFact> consumerEvents2 = Collections.synchronizedSet(new HashSet<>(EVENTS_QUANTITY));
        Set<FormRunnerFact> producerEvents = new HashSet<>(EVENTS_QUANTITY);
        kafkaClient.startConsumer(Collections.singleton(TOPIC_NAME), consumerEvents::add);
        kafkaClient2.startConsumer(Collections.singleton(TOPIC_NAME), consumerEvents2::add);

        for (int i = 0; i < EVENTS_QUANTITY; i++) {
            FormRunnerFact generatedEvent = generateEvent(i);
            producerEvents.add(generatedEvent);
            KafkaProducerClient.send(TOPIC_NAME, generatedEvent);
        }
        wait(getWaitingTime());
        Assert.assertEquals(consumerEvents, producerEvents);
        Assert.assertEquals(consumerEvents2, consumerEvents);
    }

    public synchronized void simulationTest() throws InterruptedException {
        LocalDateTime initialDate = LocalDateTime.of(2016, Calendar.FEBRUARY, 1, 0, 0, 0);
        LocalDateTime finalDate = LocalDateTime.of(2016, Calendar.MAY, 1, 23, 59, 59);

        IKafkaConsumerClient<FormRunnerFact> kafkaClient = new FormAnswerConsumer(factProvider);
        Set<FormRunnerFact> consumerEvents = Collections.synchronizedSet(new HashSet<>(EVENTS_QUANTITY));
        Set<FormRunnerFact> producerEvents = new HashSet<>(EVENTS_QUANTITY);
        kafkaClient.startConsumer(Collections.singleton(TOPIC_NAME), basicEvent -> {
            if (basicEvent.getCreationTime().isAfter(initialDate) && basicEvent.getCreationTime().isBefore(finalDate)) {
                consumerEvents.add(basicEvent);
            }
        });

        for (int i = 0; i < EVENTS_QUANTITY; i++) {
            FormRunnerFact eventInRange = generateEvent(i, initialDate, finalDate);
            producerEvents.add(eventInRange);
            KafkaProducerClient.send(TOPIC_NAME, eventInRange);
            KafkaProducerClient.send(TOPIC_NAME, generateEvent(i));
        }
        wait(getWaitingTime());
        Assert.assertEquals(consumerEvents, producerEvents);
    }

    private int getWaitingTime() {
        return 4 * EVENTS_QUANTITY > 2000 ? 4 * EVENTS_QUANTITY : 2000;
    }
}
