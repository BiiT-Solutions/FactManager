package com.biit.factmanager.client;


import com.biit.factmanager.client.exceptions.InvalidResponseException;
import com.biit.factmanager.client.fact.FactDTO;
import com.biit.rest.client.RestGenericClient;
import com.biit.rest.exceptions.EmptyResultException;
import com.biit.rest.exceptions.UnprocessableEntityException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FactClient {

    private final UrlConstructor urlConstructor;

    private final ObjectMapper mapper;

    public FactClient(UrlConstructor urlConstructor, ObjectMapper mapper) {
        this.urlConstructor = urlConstructor;
        this.mapper = mapper;
    }

    public Collection<FactDTO> post(Collection<FactDTO> facts) throws UnprocessableEntityException {
        if (facts == null || facts.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            final String result = RestGenericClient.post(urlConstructor.getFactServerUrl(), urlConstructor.addFacts(),
                    mapper.writeValueAsString(facts));
            return mapper.readValue(result, new TypeReference<List<FactDTO>>() {
            });
        } catch (JsonProcessingException e) {
            throw new InvalidResponseException(this.getClass(), e);
        } catch (EmptyResultException e) {
            return new ArrayList<>();
        }
    }

    public Collection<FactDTO> get(Map<SearchParameters, Object> filter) throws UnprocessableEntityException {
        final Map<String, Object> parameters = new HashMap<>();
        filter.forEach((searchParameters, value) -> parameters.put(searchParameters.getParamName(), value));
        try {
            final String response = RestGenericClient.get(urlConstructor.getFactServerUrl(), urlConstructor.findByParameters(), parameters);
            return mapper.readValue(response, new TypeReference<List<FactDTO>>() {
            });
        } catch (JsonProcessingException e) {
            throw new InvalidResponseException(this.getClass(), e);
        } catch (EmptyResultException e) {
            throw new RuntimeException(e);
        }
    }
}