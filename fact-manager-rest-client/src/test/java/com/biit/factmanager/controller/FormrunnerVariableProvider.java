package com.biit.factmanager.controller;

import com.biit.factmanager.client.FactClient;
import com.biit.factmanager.client.SearchParameters;
import com.biit.factmanager.client.fact.FactDTO;
import com.biit.factmanager.logger.FactManagerLogger;
import com.biit.factmanager.persistence.FormrunnerTestFact;
import com.biit.factmanager.persistence.FormrunnerTestValue;
import com.biit.rest.exceptions.UnprocessableEntityException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class FormrunnerVariableProvider {

    private final FactClient factClient;

    private final ObjectMapper mapper;

    public FormrunnerVariableProvider(FactClient factClient, ObjectMapper mapper) {
        this.factClient = factClient;
        this.mapper = mapper;
    }

    public List<FormrunnerTestFact> get(Map<SearchParameters, Object> filter) throws UnprocessableEntityException {
        List<FactDTO> rawFacts = factClient.get(filter);
        List<FormrunnerTestFact> result = new ArrayList<>();

        rawFacts.forEach(factDTO -> {
            try {
                FormrunnerTestValue value = mapper.readValue(factDTO.getValueDTO().getString(), FormrunnerTestValue.class);
                FormrunnerTestFact fact = new FormrunnerTestFact();
                BeanUtils.copyProperties(factDTO, fact);
                fact.setValue(factDTO.getValueDTO().getString());
                result.add(fact);
            } catch (JsonProcessingException e) {
                FactManagerLogger.errorMessage(this.getClass(), e);
            }

        });

        return result;
    }
}
