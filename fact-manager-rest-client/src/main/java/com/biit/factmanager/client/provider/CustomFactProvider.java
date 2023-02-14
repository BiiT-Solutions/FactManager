package com.biit.factmanager.client.provider;

import com.biit.factmanager.client.FactClient;
import com.biit.factmanager.client.IFact;
import com.biit.factmanager.client.SearchParameters;
import com.biit.factmanager.client.fact.FactDTO;
import com.biit.rest.client.Header;
import com.biit.rest.exceptions.UnprocessableEntityException;
import org.springframework.beans.BeanUtils;

import java.util.*;

public abstract class CustomFactProvider<T extends IFact> {

    private final FactClient factClient;

    private final Factory<T> factory;

    public interface Factory<T> {
        T create();
    }

    public CustomFactProvider(FactClient factClient, Factory<T> factory) {
        this.factClient = factClient;
        this.factory = factory;
    }

    T createInstance() {
        return factory.create();
    }

    public T add(T fact) throws UnprocessableEntityException {
        final List<FactDTO> rawFacts = factClient.post(Collections.singletonList(convert(fact)), null);
        if (rawFacts.size() > 0) {
            return convertDTO(rawFacts.get(0));
        }
        return null;
    }

    public List<T> add(List<T> facts) throws UnprocessableEntityException {
        final List<FactDTO> rawFacts = factClient.post(convert(facts), null);
        return convertDTO(rawFacts);
    }

    public T add(T fact, List<Header> headers) throws UnprocessableEntityException {
        final List<FactDTO> rawFacts = factClient.post(Collections.singletonList(convert(fact)), headers);
        if (rawFacts.size() > 0) {
            return convertDTO(rawFacts.get(0));
        }
        return null;
    }

    public List<T> get(Map<SearchParameters, Object> filter) throws UnprocessableEntityException {
        final List<FactDTO> rawFacts = factClient.get(filter, null);
        return convertDTO(rawFacts);
    }

    public List<T> get(Map<SearchParameters, Object> filter, List<Header> headers) throws UnprocessableEntityException {
        final List<FactDTO> rawFacts = factClient.get(filter, headers);
        return convertDTO(rawFacts);
    }

    protected T convertDTO(FactDTO factDTO) {
        final T fact = createInstance();
        BeanUtils.copyProperties(factDTO, fact);
        fact.setValue(factDTO.getValue());
        return fact;
    }

    protected List<T> convertDTO(Collection<FactDTO> factsDTO) {
        final List<T> convertedFacts = new ArrayList<>();
        factsDTO.forEach(factDTO -> convertedFacts.add(convertDTO(factDTO)));
        return convertedFacts;
    }

    protected FactDTO convert(T fact) {
        final FactDTO factDTO = new FactDTO();
        BeanUtils.copyProperties(fact, factDTO);
        factDTO.setValue(fact.getValue());
        return factDTO;
    }

    protected List<FactDTO> convert(Collection<T> facts) {
        final List<FactDTO> convertedFacts = new ArrayList<>();
        facts.forEach(fact -> convertedFacts.add(convert(fact)));
        return convertedFacts;
    }
}
