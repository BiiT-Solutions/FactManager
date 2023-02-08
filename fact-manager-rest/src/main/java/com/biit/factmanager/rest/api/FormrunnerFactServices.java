package com.biit.factmanager.rest.api;

import com.biit.factmanager.core.providers.FactProvider;
import com.biit.factmanager.persistence.entities.FormrunnerFact;
import com.biit.factmanager.persistence.entities.FormrunnerVariableFact;
import com.biit.factmanager.persistence.entities.values.FormrunnerValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(value = "/form-runner-facts")
@RestController
public class FormrunnerFactServices extends FactServices<FormrunnerValue, FormrunnerFact>{

    public FormrunnerFactServices(FactProvider<FormrunnerFact> factProvider) {
        super(factProvider);
    }

}
