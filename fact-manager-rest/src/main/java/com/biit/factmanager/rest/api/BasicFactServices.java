package com.biit.factmanager.rest.api;

import com.biit.factmanager.core.providers.FactProvider;
import com.biit.factmanager.persistence.entities.BasicFact;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(value = "/basic")
@RestController
public class BasicFactServices extends FactServices<String, BasicFact> {

    public BasicFactServices(FactProvider<BasicFact> factProvider) {
        super(factProvider);
    }
}
