package com.biit.factmanager.kafka.consumers;

import com.biit.factmanager.persistence.entities.FormRunnerFact;
import com.biit.factmanager.persistence.entities.values.FormRunnerValue;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@Configuration
public class FormAnswerConsumer2 extends FactConsumer<FormRunnerValue, FormRunnerFact> {

}
