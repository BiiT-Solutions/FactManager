package com.biit.factmanager.rest.api;

import com.biit.factmanager.core.providers.FactProvider;
import com.biit.factmanager.persistence.entities.FormrunnerQuestionFact;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(value = "/form-runner-question-facts")
@RestController
public class FormrunnerQuestionFactServices extends FactServices<FormrunnerQuestionFact> {

    public FormrunnerQuestionFactServices(FactProvider<FormrunnerQuestionFact> factProvider) {
        super(factProvider);
    }
}
