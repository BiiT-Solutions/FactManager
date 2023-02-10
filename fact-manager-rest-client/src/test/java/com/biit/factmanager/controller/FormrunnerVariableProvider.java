package com.biit.factmanager.controller;

import com.biit.factmanager.client.FactClient;
import com.biit.factmanager.client.provider.CustomFactProvider;
import com.biit.factmanager.persistence.FormrunnerTestFact;
import org.springframework.stereotype.Service;

@Service
public class FormrunnerVariableProvider extends CustomFactProvider<FormrunnerTestFact> {

    public FormrunnerVariableProvider(FactClient factClient) {
        super(factClient, FormrunnerTestFact::new);
    }
}
