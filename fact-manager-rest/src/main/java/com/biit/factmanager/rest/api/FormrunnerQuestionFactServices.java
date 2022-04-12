package com.biit.factmanager.rest.api;

import com.biit.factmanager.core.providers.FactProvider;
import com.biit.factmanager.kafka.producers.FormAnswerProducer;
import com.biit.factmanager.logger.FactManagerLogger;
import com.biit.factmanager.persistence.entities.FormrunnerQuestionFact;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RequestMapping(value = "/form-runner-question-facts")
@RestController
public class FormrunnerQuestionFactServices extends FactServices<FormrunnerQuestionFact> {

    @Autowired
    public FormrunnerQuestionFactServices(FactProvider<FormrunnerQuestionFact> factProvider) {
        super(factProvider);
    }

    @Autowired
    private FormAnswerProducer formAnswerProducer;

    @Operation(summary = "Sends a list of FormrunnerQuestionFacts", description = "Parameters:\n"
            + "facts (required): List of Fact objects to be added")
    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping(value = "/kafka-send", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<FormrunnerQuestionFact> addFactList(@Parameter(name = "Notification Request", required = true)
                                                        @RequestBody List<FormrunnerQuestionFact> formrunnerQuestionFacts,
                                                    HttpServletRequest httpRequest) {
        FactManagerLogger.debug(this.getClass().getName(), "Sending a list of facts '{}'.", formrunnerQuestionFacts);
        //logic in here because of cyclic dependency if a Kafka provider is created
        formrunnerQuestionFacts.forEach(formrunnerQuestionFact -> {
            formAnswerProducer.sendFact(formrunnerQuestionFact);
        });
        return formrunnerQuestionFacts;
    }
}
