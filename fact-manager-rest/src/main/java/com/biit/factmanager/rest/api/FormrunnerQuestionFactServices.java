package com.biit.factmanager.rest.api;

import com.biit.factmanager.core.providers.FactProvider;
import com.biit.factmanager.persistence.entities.FormrunnerQuestionFact;
import com.biit.factmanager.persistence.entities.values.FormrunnerQuestionValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(value = "/form-runner-question-facts")
@RestController
public class FormrunnerQuestionFactServices extends FactServices<FormrunnerQuestionValue, FormrunnerQuestionFact> {

    protected final String DISCRIMINATOR_VALUE = new FormrunnerQuestionFact().getDiscriminatorValue();

    public FormrunnerQuestionFactServices(FactProvider<FormrunnerQuestionFact> factProvider) {
        super(factProvider);
    }

    @Override
    public String getDiscriminatorValue() {
        return DISCRIMINATOR_VALUE;
    }
}
