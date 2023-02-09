package com.biit.factmanager.rest.api;

import com.biit.factmanager.core.providers.FactProvider;
import com.biit.factmanager.persistence.entities.StringFact;
import com.biit.factmanager.persistence.entities.values.StringValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(value = "/string")
@RestController
public class StringFactServices extends FactServices<StringValue, StringFact> {

    protected final String DISCRIMINATOR_VALUE = new StringFact().getDiscriminatorValue();

    public StringFactServices(FactProvider<StringFact> factProvider) {
        super(factProvider);
    }

    @Override
    public String getDiscriminatorValue() {
        return DISCRIMINATOR_VALUE;
    }
}
