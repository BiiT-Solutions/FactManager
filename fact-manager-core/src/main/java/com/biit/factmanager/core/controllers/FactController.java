package com.biit.factmanager.core.controllers;


import com.biit.factmanager.core.controllers.models.FactDTO;
import com.biit.factmanager.core.converters.FactConverter;
import com.biit.factmanager.core.converters.models.FactConverterRequest;
import com.biit.factmanager.core.providers.FactProvider;
import com.biit.factmanager.persistence.entities.Fact;
import com.biit.factmanager.persistence.repositories.FactRepository;
import com.biit.server.controller.BasicInsertableController;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class FactController<ENTITY> extends BasicInsertableController<Fact<ENTITY>, FactDTO<ENTITY>, FactRepository<Fact<ENTITY>>,
        FactProvider<Fact<ENTITY>>, FactConverterRequest<ENTITY>, FactConverter<ENTITY>> {

    @Autowired
    protected FactController(FactProvider<Fact<ENTITY>> provider, FactConverter<ENTITY> converter) {
        super(provider, converter);
    }

    @Override
    protected FactConverterRequest<ENTITY> createConverterRequest(Fact<ENTITY> entity) {
        return new FactConverterRequest<>(entity);
    }
}
