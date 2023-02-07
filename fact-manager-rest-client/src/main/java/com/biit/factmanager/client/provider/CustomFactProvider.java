package com.biit.factmanager.client.provider;

import com.biit.factmanager.client.FactClient;
import com.biit.factmanager.client.IFact;
import com.biit.factmanager.client.SearchParameters;
import com.biit.factmanager.client.fact.FactDTO;
import com.biit.rest.client.Header;
import com.biit.rest.exceptions.UnprocessableEntityException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class CustomFactProvider<T extends IFact> {

    private final FactClient factClient;

    private final ObjectMapper mapper;

    private final Factory<T> factory;

    public interface Factory<T> {
        T create();
    }

    public CustomFactProvider(FactClient factClient, ObjectMapper mapper, Factory<T> factory) {
        this.factClient = factClient;
        this.mapper = mapper;
        this.factory = factory;
    }

    T createInstance() {
        return factory.create();
    }

//    public List<T> add(T fact, List<Header> headers) throws UnprocessableEntityException {
//        final List<FactDTO> rawFacts = factClient.get(filter, headers);
//        final List<T> result = new ArrayList<>();
//
//        rawFacts.forEach(factDTO -> {
//            final T fact = createInstance();
//            BeanUtils.copyProperties(factDTO, fact);
//            fact.setValue(factDTO.getValueDTO().getString());
//            result.add(fact);
//        });
//
//        return result;
//    }

    public List<T> get(Map<SearchParameters, Object> filter, List<Header> headers) throws UnprocessableEntityException {
        final List<FactDTO> rawFacts = factClient.get(filter, headers);
        final List<T> result = new ArrayList<>();

        rawFacts.forEach(factDTO -> {
            final T fact = createInstance();
            BeanUtils.copyProperties(factDTO, fact);
            fact.setValue(factDTO.getValueDTO().getString());
            result.add(fact);
        });

        return result;
    }
}
