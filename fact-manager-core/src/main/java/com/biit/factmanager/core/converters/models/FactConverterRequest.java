package com.biit.factmanager.core.converters.models;

import com.biit.factmanager.persistence.entities.Fact;
import com.biit.server.converters.models.ConverterRequest;

public class FactConverterRequest<ENTITY> extends ConverterRequest<Fact<ENTITY>> {
    public FactConverterRequest(Fact<ENTITY> entity) {
        super(entity);
    }
}
