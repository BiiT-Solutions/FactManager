package com.biit.factmanager.rest.api;

import com.biit.factmanager.core.providers.FactProvider;
import com.biit.factmanager.persistence.entities.LogFact;
import com.biit.factmanager.persistence.entities.values.StringValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(value = "/string")
@RestController
public class StringFactServices extends FactServices<StringValue, LogFact> {

    public StringFactServices(FactProvider<LogFact> factProvider) {
        super(factProvider);
    }
}
