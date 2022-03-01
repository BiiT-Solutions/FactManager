package com.biit.factmanager.test;

import com.biit.factmanager.core.providers.FactProvider;
import com.biit.factmanager.kafka.consumers.FormAnswerConsumer;
import com.biit.factmanager.kafka.consumers.FormAnswerConsumer2;
import com.biit.factmanager.kafka.consumers.FormConsumerListeners;
import com.biit.factmanager.kafka.consumers.FormConsumerListeners2;
import com.biit.factmanager.kafka.producers.FormAnswerProducer;
import com.biit.factmanager.kafka.producers.FormAnswerProducer2;
import com.biit.factmanager.persistence.entities.FormRunnerFact;
import com.biit.factmanager.persistence.entities.values.FormRunnerValue;
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

    @Autowired
    private FormAnswerProducer formAnswerProducer;

    @Autowired
    private FormConsumerListeners<FormRunnerValue, FormRunnerFact> formConsumerListeners;
    @Autowired
    private FormConsumerListeners2<FormRunnerValue, FormRunnerFact> formConsumerListeners2;

    //@Autowired
    private FormAnswerProducer2 formAnswerProducer2;

    @Autowired
    private FormAnswerConsumer formAnswerConsumer;

    //@Autowired
    private FormAnswerConsumer2 formAnswerConsumer2;

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
        Set<FormRunnerFact> consumerEvents = Collections.synchronizedSet(new HashSet<>(EVENTS_QUANTITY));
        Set<FormRunnerFact> producerEvents = new HashSet<>(EVENTS_QUANTITY);
        //Store received events into set.
        formConsumerListeners.addListener(fact -> consumerEvents.add((FormRunnerFact) fact));

        for (int i = 0; i < EVENTS_QUANTITY; i++) {
            FormRunnerFact generatedEvent = generateEvent(i);
            producerEvents.add(generatedEvent);
            formAnswerProducer.sendFact(generatedEvent);
        }

        wait(getWaitingTime());
        Assert.assertEquals(consumerEvents, producerEvents);
    }

    public synchronized void multipleProducerTest() throws InterruptedException {
        Set<FormRunnerFact> consumerEvents = Collections.synchronizedSet(new HashSet<>(EVENTS_QUANTITY * 2));
        Set<FormRunnerFact> producerEvents = new HashSet<>(EVENTS_QUANTITY);
        Set<FormRunnerFact> producerEvents2 = new HashSet<>(EVENTS_QUANTITY);
        formConsumerListeners.addListener(fact -> consumerEvents.add((FormRunnerFact) fact));
        for (int i = 0; i < EVENTS_QUANTITY; i++) {
            FormRunnerFact generatedEvent = generateEvent(i);
            producerEvents.add(generatedEvent);
            formAnswerProducer.sendFact(generatedEvent);
            FormRunnerFact generatedEvent2 = generateEvent(i);
            producerEvents2.add(generatedEvent2);
            formAnswerProducer2.sendFact(generatedEvent2);
        }
        producerEvents.addAll(producerEvents2);
        wait(getWaitingTime());
        Assert.assertEquals(consumerEvents, producerEvents);
    }

    public synchronized void multipleConsumerTest() throws InterruptedException {
        Set<FormRunnerFact> consumerEvents = Collections.synchronizedSet(new HashSet<>(EVENTS_QUANTITY));
        Set<FormRunnerFact> consumerEvents2 = Collections.synchronizedSet(new HashSet<>(EVENTS_QUANTITY));
        Set<FormRunnerFact> producerEvents = new HashSet<>(EVENTS_QUANTITY);
        formConsumerListeners.addListener(fact -> consumerEvents.add((FormRunnerFact) fact));
        formConsumerListeners2.addListener(fact -> consumerEvents.add((FormRunnerFact) fact));

        for (int i = 0; i < EVENTS_QUANTITY; i++) {
            FormRunnerFact generatedEvent = generateEvent(i);
            producerEvents.add(generatedEvent);
            formAnswerProducer.sendFact(generatedEvent);
        }
        wait(getWaitingTime());
        Assert.assertEquals(consumerEvents, producerEvents);
        Assert.assertEquals(consumerEvents2, consumerEvents);
    }

    public synchronized void simulationTest() throws InterruptedException {
        LocalDateTime initialDate = LocalDateTime.of(2022, Calendar.FEBRUARY, 1, 0, 0, 0);
        LocalDateTime finalDate = LocalDateTime.of(2022, Calendar.MAY, 1, 23, 59, 59);

        Set<FormRunnerFact> consumerEvents = Collections.synchronizedSet(new HashSet<>(EVENTS_QUANTITY));
        Set<FormRunnerFact> producerEvents = new HashSet<>(EVENTS_QUANTITY);
        formConsumerListeners.addListener(fact -> {
            if (fact.getCreationTime().isAfter(initialDate) && fact.getCreationTime().isBefore(finalDate)) {
                consumerEvents.add((FormRunnerFact) fact);
            }
        });

        for (int i = 0; i < EVENTS_QUANTITY; i++) {
            FormRunnerFact eventInRange = generateEvent(i, initialDate, finalDate);
            producerEvents.add(eventInRange);
            formAnswerProducer.sendFact(eventInRange);
            formAnswerProducer.sendFact(eventInRange);
        }
        wait(getWaitingTime());
        Assert.assertEquals(consumerEvents, producerEvents);
    }

    private int getWaitingTime() {
        return 4 * EVENTS_QUANTITY > 2000 ? 4 * EVENTS_QUANTITY : 2000;
    }
}
