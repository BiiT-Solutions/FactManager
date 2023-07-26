package com.biit.factmanager.client.provider;

import com.biit.factmanager.client.FactClient;
import com.biit.factmanager.client.SearchParameters;
import com.biit.factmanager.client.fact.FactDTO;
import com.biit.rest.client.Header;
import com.biit.rest.exceptions.UnprocessableEntityException;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class ClientFactProvider {

    private final FactClient factClient;

    private final Factory<FactDTO> factory;

    public interface Factory<FactDTO> {
        FactDTO create();
    }

    public ClientFactProvider(FactClient factClient) {
        this.factClient = factClient;
        this.factory = FactDTO::new;
    }

    FactDTO createInstance() {
        return factory.create();
    }

    public FactDTO add(FactDTO fact) throws UnprocessableEntityException {
        final List<FactDTO> rawFacts = factClient.post(Collections.singletonList(convert(fact)), null);
        if (rawFacts.size() > 0) {
            return convertDTO(rawFacts.get(0));
        }
        return null;
    }

    public List<FactDTO> add(List<FactDTO> facts) throws UnprocessableEntityException {
        final List<FactDTO> rawFacts = factClient.post(convert(facts), null);
        return convertDTO(rawFacts);
    }

    public FactDTO add(FactDTO fact, List<Header> headers) throws UnprocessableEntityException {
        final List<FactDTO> rawFacts = factClient.post(Collections.singletonList(convert(fact)), headers);
        if (rawFacts.size() > 0) {
            return convertDTO(rawFacts.get(0));
        }
        return null;
    }

    public List<FactDTO> get(Map<SearchParameters, Object> filter) throws UnprocessableEntityException {
        final List<FactDTO> rawFacts = factClient.get(filter, null);
        return convertDTO(rawFacts);
    }

    public List<FactDTO> get(Map<SearchParameters, Object> filter, List<Header> headers) throws UnprocessableEntityException {
        final List<FactDTO> rawFacts = factClient.get(filter, headers);
        return convertDTO(rawFacts);
    }

    protected FactDTO convertDTO(FactDTO factDTO) {
        final FactDTO fact = createInstance();
        BeanUtils.copyProperties(factDTO, fact);
        fact.setValue(factDTO.getValue());
        return fact;
    }

    protected List<FactDTO> convertDTO(Collection<FactDTO> factsDTO) {
        final List<FactDTO> convertedFacts = new ArrayList<>();
        factsDTO.forEach(factDTO -> convertedFacts.add(convertDTO(factDTO)));
        return convertedFacts;
    }

    protected FactDTO convert(FactDTO fact) {
        final FactDTO factDTO = new FactDTO();
        BeanUtils.copyProperties(fact, factDTO);
        factDTO.setValue(fact.getValue());
        return factDTO;
    }

    protected List<FactDTO> convert(Collection<FactDTO> facts) {
        final List<FactDTO> convertedFacts = new ArrayList<>();
        facts.forEach(fact -> convertedFacts.add(convert(fact)));
        return convertedFacts;
    }
}
