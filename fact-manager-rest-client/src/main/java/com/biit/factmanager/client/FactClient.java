package com.biit.factmanager.client;


import com.biit.factmanager.dto.FactDTO;
import com.biit.factmanager.logger.FactClientLogger;
import com.biit.rest.client.Header;
import com.biit.rest.exceptions.EmptyResultException;
import com.biit.rest.exceptions.InvalidResponseException;
import com.biit.rest.exceptions.UnprocessableEntityException;
import com.biit.server.client.SecurityClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FactClient {

    private final FactUrlConstructor factUrlConstructor;

    private final ObjectMapper mapper;

    private final String applicationName;

    private final String customerName;

    private final SecurityClient securityClient;

    public FactClient(@Value("${spring.application.name:null}") String applicationName, @Value("${facts.customer:null}") String customerName,
                      FactUrlConstructor factUrlConstructor, ObjectMapper mapper, SecurityClient securityClient) {
        this.factUrlConstructor = factUrlConstructor;
        this.mapper = mapper;
        this.applicationName = applicationName;
        this.customerName = customerName;
        this.securityClient = securityClient;
    }

    public List<FactDTO> post(Collection<FactDTO> facts, List<Header> headers) throws UnprocessableEntityException {
        if (facts == null || facts.isEmpty()) {
            return new ArrayList<>();
        }
        facts.forEach(factDTO -> {
            factDTO.setCustomer(customerName);
            factDTO.setApplication(applicationName);
        });
        try {
            try (Response result = securityClient.post(factUrlConstructor.getFactServerUrl(), factUrlConstructor.addFacts(),
                    mapper.writeValueAsString(facts), headers)) {
                final String res = result.readEntity(String.class);
                FactClientLogger.debug(this.getClass(), "Response obtained from '{}' is '{}'.",
                        factUrlConstructor.getFactServerUrl() + factUrlConstructor.addFacts(), result.getStatus());
                return mapper.readValue(res, new TypeReference<List<FactDTO>>() {
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
//        filter.putIfAbsent(SearchParameters.CUSTOMER, customerName);
//        filter.putIfAbsent(SearchParameters.APPLICATION, applicationName);
        try {
            try (Response result = securityClient.get(factUrlConstructor.getFactServerUrl(),
                    factUrlConstructor.findByParameters(), parameters, headers)) {
                FactClientLogger.debug(this.getClass(), "Response obtained from '{}' is '{}'.",
                        factUrlConstructor.getFactServerUrl() + factUrlConstructor.findByParameters(), result.getStatus());
                return mapper.readValue(result.readEntity(String.class), new TypeReference<>() {
                });
            }
        } catch (JsonProcessingException e) {
            throw new InvalidResponseException(e);
        } catch (EmptyResultException e) {
            throw new RuntimeException(e);
        }
    }
}
