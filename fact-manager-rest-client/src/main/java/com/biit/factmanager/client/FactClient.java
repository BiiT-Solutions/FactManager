package com.biit.factmanager.client;


import com.biit.factmanager.client.fact.FactDTO;
import com.biit.rest.client.Header;
import com.biit.rest.client.RestGenericClient;
import com.biit.rest.exceptions.EmptyResultException;
import com.biit.rest.exceptions.InvalidResponseException;
import com.biit.rest.exceptions.UnprocessableEntityException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.Response;
import java.util.*;

@Service
public class FactClient {

    private final FactUrlConstructor factUrlConstructor;

    private final ObjectMapper mapper;

    public FactClient(FactUrlConstructor factUrlConstructor, ObjectMapper mapper) {
        this.factUrlConstructor = factUrlConstructor;
        this.mapper = mapper;
    }

    public List<FactDTO> post(Collection<FactDTO> facts) throws UnprocessableEntityException {
        if (facts == null || facts.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            try (final Response result = RestGenericClient.post(factUrlConstructor.getFactServerUrl(), factUrlConstructor.addFacts(),
                    mapper.writeValueAsString(facts))) {
                return mapper.readValue(result.readEntity(String.class), new TypeReference<List<FactDTO>>() {
                });
            }
        } catch (JsonProcessingException e) {
            throw new InvalidResponseException(e);
        } catch (EmptyResultException e) {
            return new ArrayList<>();
        }
    }

    public List<FactDTO> get(Map<SearchParameters, Object> filter, List<Header> headers) throws UnprocessableEntityException {
        final Map<String, Object> parameters = new HashMap<>();
        filter.forEach((searchParameters, value) -> parameters.put(searchParameters.getParamName(), value));
        try {
            try (final Response response = RestGenericClient.get(factUrlConstructor.getFactServerUrl(),
                    factUrlConstructor.findByParameters(), parameters, headers)) {
                return mapper.readValue(response.readEntity(String.class), new TypeReference<List<FactDTO>>() {
                });
            }
        } catch (JsonProcessingException e) {
            throw new InvalidResponseException(e);
        } catch (EmptyResultException e) {
            throw new RuntimeException(e);
        }
    }
}