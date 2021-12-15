package com.biit.factmanager.rest.api;

import com.biit.factmanager.core.providers.FactProvider;
import com.biit.factmanager.persistence.entities.FormRunnerFact;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(value = "/form-runner-facts")
@RestController
public class FormRunnerFactServices extends FactServices<FormRunnerFact> {

    @Autowired
    public FormRunnerFactServices(FactProvider<FormRunnerFact> factProvider) {
        super(factProvider);
    }
}
