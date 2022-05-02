package com.biit.factmanager.test;

import com.biit.factmanager.core.providers.FactProvider;
import com.biit.factmanager.kafka.consumers.FormAnswerConsumer;
import com.biit.factmanager.kafka.consumers.FormAnswerConsumer2;
import com.biit.factmanager.kafka.consumers.FormAnswerConsumerListeners;
import com.biit.factmanager.kafka.consumers.FormAnswerConsumerListeners2;
import com.biit.factmanager.kafka.producers.FormAnswerProducer;
import com.biit.factmanager.kafka.producers.FormAnswerProducer2;
import com.biit.factmanager.persistence.entities.FormrunnerQuestionFact;
import com.biit.factmanager.persistence.entities.values.FormrunnerQuestionValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
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
    private FactProvider<FormrunnerQuestionFact> factProvider;

    @Autowired
    private FormAnswerProducer formAnswerProducer;

    @Autowired
    private FormAnswerConsumerListeners formAnswerConsumerListeners;

    @Autowired
    private FormAnswerConsumerListeners2 formAnswerConsumerListeners2;

    @Autowired
    private FormAnswerProducer2 formAnswerProducer2;

    @Autowired
    private FormAnswerConsumer formAnswerConsumer;

    @Autowired
    private FormAnswerConsumer2 formAnswerConsumer2;

    private ObjectMapper objectMapper;

    private FormrunnerQuestionFact generateEvent(int value) {
        FormrunnerQuestionFact FormrunnerQuestionFact = new FormrunnerQuestionFact();
        FormrunnerQuestionValue FormrunnerQuestionValue = new FormrunnerQuestionValue();
        FormrunnerQuestionValue.setQuestion(QUESTION + value);
        FormrunnerQuestionValue.setAnswer(ANSWER + value);
        FormrunnerQuestionFact.setEntity(FormrunnerQuestionValue);
        return FormrunnerQuestionFact;
    }

    public FormrunnerQuestionFact generateEvent(int value, LocalDateTime minTimestamp, LocalDateTime maxTimestamp) {
        FormrunnerQuestionFact FormrunnerQuestionFact = generateEvent(value);

        //Create a random day.
        // create ZoneId
        ZoneOffset zone = ZoneOffset.of("Z");
        long randomSecond = ThreadLocalRandom.current().nextLong(minTimestamp.toEpochSecond(zone), maxTimestamp.toEpochSecond(zone));
        LocalDateTime randomDate = LocalDateTime.ofEpochSecond(randomSecond, 0, zone);

        FormrunnerQuestionFact.setCreatedAt(randomDate);
        return FormrunnerQuestionFact;
    }

    private ObjectMapper getObjectMapper() {
        if (objectMapper == null) {
            final JavaTimeModule module = new JavaTimeModule();
            LocalDateTimeDeserializer localDateTimeDeserializer = new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
            module.addDeserializer(LocalDateTime.class, localDateTimeDeserializer);
            objectMapper = Jackson2ObjectMapperBuilder.json()
                    .modules(module)
                    .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                    .build();
        }
        return objectMapper;
    }

    public void checkDeserializer() throws JsonProcessingException {
        String value = "{\"value\":\"{\\\"answer\\\":\\\"Answer: 0\\\",\\\"question\\\":\\\"Question? 0\\\"}\",\"createdAt\":\"03-03-2022 16:10:23\",\"entity\":{\"answer\":\"Answer: 0\",\"question\":\"Question? 0\"},\"createdAt\":\"03-03-2022 16:10:24\"}";
        getObjectMapper().readValue(value, FormrunnerQuestionFact.class);
        //No serialization exception found.
    }

    public synchronized void factTest() throws InterruptedException {
        Set<FormrunnerQuestionFact> consumerEvents = Collections.synchronizedSet(new HashSet<>(EVENTS_QUANTITY));
        Set<FormrunnerQuestionFact> producerEvents = new HashSet<>(EVENTS_QUANTITY);
        //Store received events into set.
        formAnswerConsumerListeners.addListener(consumerEvents::add);

        for (int i = 0; i < EVENTS_QUANTITY; i++) {
            FormrunnerQuestionFact generatedEvent = generateEvent(i);
            producerEvents.add(generatedEvent);
            formAnswerProducer.sendFact(generatedEvent);
        }

        wait(consumerEvents);
        Assert.assertEquals(consumerEvents.size(), producerEvents.size());
        Assert.assertEquals(consumerEvents, producerEvents);
    }

    public synchronized void multipleProducerTest() throws InterruptedException {
        Set<FormrunnerQuestionFact> consumerEvents = Collections.synchronizedSet(new HashSet<>(EVENTS_QUANTITY * 2));
        Set<FormrunnerQuestionFact> producerEvents = new HashSet<>(EVENTS_QUANTITY);
        Set<FormrunnerQuestionFact> producerEvents2 = new HashSet<>(EVENTS_QUANTITY);
        formAnswerConsumerListeners.addListener(consumerEvents::add);
        for (int i = 0; i < EVENTS_QUANTITY; i++) {
            FormrunnerQuestionFact generatedEvent = generateEvent(i);
            producerEvents.add(generatedEvent);
            formAnswerProducer.sendFact(generatedEvent);
            FormrunnerQuestionFact generatedEvent2 = generateEvent(i);
            producerEvents2.add(generatedEvent2);
            formAnswerProducer2.sendFact(generatedEvent2);
        }
        producerEvents.addAll(producerEvents2);
        wait(consumerEvents);
        Assert.assertEquals(consumerEvents, producerEvents);
    }

    public synchronized void multipleConsumerTest() throws InterruptedException {
        Set<FormrunnerQuestionFact> consumerEvents = Collections.synchronizedSet(new HashSet<>(EVENTS_QUANTITY));
        Set<FormrunnerQuestionFact> consumerEvents2 = Collections.synchronizedSet(new HashSet<>(EVENTS_QUANTITY));
        Set<FormrunnerQuestionFact> producerEvents = new HashSet<>(EVENTS_QUANTITY);
        formAnswerConsumerListeners.addListener(consumerEvents::add);
        formAnswerConsumerListeners2.addListener(consumerEvents2::add);

        for (int i = 0; i < EVENTS_QUANTITY; i++) {
            FormrunnerQuestionFact generatedEvent = generateEvent(i);
            producerEvents.add(generatedEvent);
            formAnswerProducer.sendFact(generatedEvent);
        }
        wait(consumerEvents);
        Assert.assertEquals(consumerEvents, producerEvents);
        Assert.assertEquals(consumerEvents2, producerEvents);
    }

    public synchronized void simulationTest() throws InterruptedException {
        LocalDateTime initialDate = LocalDateTime.of(2022, Calendar.FEBRUARY, 1, 0, 0, 0);
        LocalDateTime finalDate = LocalDateTime.of(2022, Calendar.MAY, 1, 23, 59, 59);

        Set<FormrunnerQuestionFact> consumerEvents = Collections.synchronizedSet(new HashSet<>(EVENTS_QUANTITY));
        Set<FormrunnerQuestionFact> producerEvents = new HashSet<>(EVENTS_QUANTITY);
        formAnswerConsumerListeners.addListener(fact -> {
            if (fact.getCreationTime().isAfter(initialDate) && fact.getCreationTime().isBefore(finalDate)) {
                consumerEvents.add((FormrunnerQuestionFact) fact);
            }
        });

        for (int i = 0; i < EVENTS_QUANTITY; i++) {
            FormrunnerQuestionFact eventInRange = generateEvent(i, initialDate, finalDate);
            producerEvents.add(eventInRange);
            formAnswerProducer.sendFact(eventInRange);
            formAnswerProducer.sendFact(eventInRange);
        }
        wait(consumerEvents);
        Assert.assertEquals(consumerEvents, producerEvents);
    }

    private void wait(Set<FormrunnerQuestionFact> consumerEvents) throws InterruptedException {
        int i = 0;
        do {
            wait(1000);
            i++;
        } while (consumerEvents.size() < EVENTS_QUANTITY && i < 10);
    }
}
