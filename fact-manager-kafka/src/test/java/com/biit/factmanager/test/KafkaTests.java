package com.biit.factmanager.test;

import com.biit.factmanager.core.providers.FactProvider;
import com.biit.factmanager.persistence.entities.FormrunnerQuestionFact;
import com.biit.factmanager.persistence.entities.values.FormrunnerQuestionValue;
import com.biit.factmanager.test.listener.TestEventListener;
import com.biit.kafka.consumers.EventListener;
import com.biit.kafka.events.Event;
import com.biit.kafka.events.KafkaEventTemplate;
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

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static org.awaitility.Awaitility.await;

@SpringBootTest
@Test(groups = {"kafkaEvents"})
public class KafkaTests extends AbstractTransactionalTestNGSpringContextTests {
    private static final int EVENTS_WAITING_TIME = 100;
    private static final int EVENTS_QUANTITY = 100;
    private static final String QUESTION = "/form/category/questionA ";
    private static final String ANSWER = "Answer: ";

    @Autowired
    private FactProvider<FormrunnerQuestionFact> factProvider;

    @Autowired
    private KafkaEventTemplate kafkaTemplate;

    @Autowired
    private EventListener eventListener;

    @Autowired
    private TestEventListener testEventListener;

    private ObjectMapper objectMapper;

    private FormrunnerQuestionValue generatePayload(int value) {
        FormrunnerQuestionValue FormrunnerQuestionValue = new FormrunnerQuestionValue();
        FormrunnerQuestionValue.setXpath(QUESTION + value);
        FormrunnerQuestionValue.setAnswer(ANSWER + value);
        return FormrunnerQuestionValue;
    }

    private Event generateEvent(int value) {
        return new Event(generatePayload(value));
    }

    private FormrunnerQuestionFact generateFact(int value) {
        FormrunnerQuestionFact formrunnerQuestionFact = new FormrunnerQuestionFact();
        formrunnerQuestionFact.setEntity(generatePayload(value));
        return formrunnerQuestionFact;
    }

    private Event generateEvent(int value, LocalDateTime minTimestamp, LocalDateTime maxTimestamp) {
        Event formrunnerQuestionEvent = new Event(generatePayload(value));

        //Create a random day.
        // create ZoneId
        ZoneOffset zone = ZoneOffset.of("Z");
        long randomSecond = ThreadLocalRandom.current().nextLong(minTimestamp.toEpochSecond(zone), maxTimestamp.toEpochSecond(zone));
        LocalDateTime randomDate = LocalDateTime.ofEpochSecond(randomSecond, 0, zone);

        formrunnerQuestionEvent.setCreatedAt(randomDate);
        return formrunnerQuestionEvent;
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

    @Test
    public void checkDeserializer() throws JsonProcessingException {
        String value = "{\"value\":\"{\\\"answer\\\":\\\"Answer: 0\\\",\\\"question\\\":\\\"Question? 0\\\"}\",\"createdAt\":\"03-03-2022 16:10:23\",\"entity\":{\"answer\":\"Answer: 0\",\"question\":\"Question? 0\"},\"createdAt\":\"03-03-2022 16:10:24\"}";
        getObjectMapper().readValue(value, FormrunnerQuestionFact.class);
        //No serialization exception found.
    }

    @Test
    public synchronized void factTest() {
        Set<Event> consumerEvents = Collections.synchronizedSet(new HashSet<>(EVENTS_QUANTITY));
        Set<Event> producerEvents = new HashSet<>(EVENTS_QUANTITY);
        //Store received events into set.
        eventListener.addListener((event, offset, key, partition, topic, timeStamp) -> consumerEvents.add(event));

        for (int i = 0; i < EVENTS_QUANTITY; i++) {
            FormrunnerQuestionFact generatedFact = generateFact(i);
            Event event = new Event(generatedFact);
            producerEvents.add(event);
            kafkaTemplate.send(event);
        }

        await().atMost(Duration.ofSeconds(EVENTS_WAITING_TIME)).untilAsserted(() -> {
            Assert.assertEquals(consumerEvents.size(), producerEvents.size());
            Assert.assertEquals(consumerEvents, producerEvents);
        });
    }

    @Test
    public synchronized void multipleProducerTest() {
        Set<Event> consumerEvents = Collections.synchronizedSet(new HashSet<>(EVENTS_QUANTITY * 2));
        Set<Event> producerEvents = new HashSet<>(EVENTS_QUANTITY);
        Set<Event> producerEvents2 = new HashSet<>(EVENTS_QUANTITY);

        eventListener.addListener((event, offset, key, partition, topic, timeStamp) -> consumerEvents.add(event));

        for (int i = 0; i < EVENTS_QUANTITY; i++) {
            FormrunnerQuestionFact generatedFact = generateFact(i);
            Event event = new Event(generatedFact);
            producerEvents.add(event);
            kafkaTemplate.send(event);
            FormrunnerQuestionFact generatedEvent2 = generateFact(i);
            event = new Event(generatedEvent2);
            producerEvents2.add(event);
            kafkaTemplate.send(event);
        }
        producerEvents.addAll(producerEvents2);
        //Check both listeners read the same event.
        await().atMost(Duration.ofSeconds(EVENTS_WAITING_TIME)).untilAsserted(() -> {
            Assert.assertEquals(consumerEvents.size(), producerEvents.size());
            Assert.assertEquals(consumerEvents, producerEvents);
        });
    }

    @Test
    public synchronized void multipleConsumerTest() {
        Set<Event> consumerEvents = Collections.synchronizedSet(new HashSet<>(EVENTS_QUANTITY));
        Set<Event> consumerEvents2 = Collections.synchronizedSet(new HashSet<>(EVENTS_QUANTITY));
        Set<Event> producerEvents = new HashSet<>(EVENTS_QUANTITY);

        eventListener.addListener((event, offset, key, partition, topic, timeStamp) -> consumerEvents.add(event));
        testEventListener.addListener((event, offset, key, partition, topic, timeStamp) -> consumerEvents2.add(event));

        for (int i = 0; i < EVENTS_QUANTITY; i++) {
            FormrunnerQuestionValue generatedFact = generatePayload(i);
            Event event = new Event(generatedFact);
            producerEvents.add(event);
            kafkaTemplate.send(event);
        }

        await().atMost(Duration.ofSeconds(EVENTS_WAITING_TIME)).untilAsserted(() -> {
            Assert.assertEquals(consumerEvents.size(), producerEvents.size());
            Assert.assertEquals(consumerEvents2.size(), producerEvents.size());
            Assert.assertEquals(consumerEvents, producerEvents);
            Assert.assertEquals(consumerEvents2, producerEvents);
        });
    }

    @Test
    public synchronized void simulationTest() throws InterruptedException {
        LocalDateTime initialDate = LocalDateTime.of(2022, Calendar.FEBRUARY, 1, 0, 0, 0);
        LocalDateTime finalDate = LocalDateTime.of(2022, Calendar.MAY, 1, 23, 59, 59);

        Set<Event> consumerEvents = Collections.synchronizedSet(new HashSet<>(EVENTS_QUANTITY));
        Set<Event> producerEvents = new HashSet<>(EVENTS_QUANTITY);

        eventListener.addListener((event, offset, key, partition, topic, timeStamp) -> {
            if (event.getCreatedAt().isAfter(initialDate) && event.getCreatedAt().isBefore(finalDate)) {
                consumerEvents.add(event);
            }
        });

        for (int i = 0; i < EVENTS_QUANTITY; i++) {
            Event eventInRange = generateEvent(i, initialDate, finalDate);
            producerEvents.add(eventInRange);
            kafkaTemplate.send(eventInRange);
        }

        await().atMost(Duration.ofSeconds(EVENTS_WAITING_TIME)).untilAsserted(() -> {
            Assert.assertEquals(consumerEvents.size(), producerEvents.size());
            Assert.assertEquals(consumerEvents, producerEvents);
        });
    }
}
