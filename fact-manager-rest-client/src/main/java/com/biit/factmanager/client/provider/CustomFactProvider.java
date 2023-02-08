package com.biit.factmanager.client.provider;

import com.biit.factmanager.client.FactClient;
import com.biit.factmanager.client.IFact;
import com.biit.factmanager.client.SearchParameters;
import com.biit.factmanager.client.fact.FactDTO;
import com.biit.rest.client.Header;
import com.biit.rest.exceptions.UnprocessableEntityException;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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

    public List<T> add(T fact, List<Header> headers) throws UnprocessableEntityException {
        final List<FactDTO> rawFacts = factClient.post(Collections.singletonList(convert(fact)), headers);

        final List<T> result = new ArrayList<>();
        rawFacts.forEach(factDTO -> result.add(convert(factDTO)));
        return result;
    }

    public List<T> get(Map<SearchParameters, Object> filter, List<Header> headers) throws UnprocessableEntityException {
        final List<FactDTO> rawFacts = factClient.get(filter, headers);

        final List<T> result = new ArrayList<>();
        rawFacts.forEach(factDTO -> result.add(convert(factDTO)));
        return result;
    }

    protected T convert(FactDTO factDTO) {
        final T fact = createInstance();
        BeanUtils.copyProperties(factDTO, fact);
        fact.setValue(factDTO.getValue());
        return fact;
    }

    protected FactDTO convert(T fact) {
        final FactDTO factDTO = new FactDTO();
        BeanUtils.copyProperties(fact, factDTO);
        factDTO.setValue(fact.getValue());
        return factDTO;
    }
}
