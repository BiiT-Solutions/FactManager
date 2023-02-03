package com.biit.factmanager.rest.api;

import com.biit.factmanager.core.providers.FactProvider;
import com.biit.factmanager.persistence.entities.StringFact;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(value = "/string")
@RestController
public class StringFactServices extends FactServices<StringFact> {

    public StringFactServices(FactProvider<StringFact> factProvider) {
        super(factProvider);
    }
}
