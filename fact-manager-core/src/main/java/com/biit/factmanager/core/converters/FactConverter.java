package com.biit.factmanager.core.converters;

import com.biit.factmanager.core.controllers.models.FactDTO;
import com.biit.factmanager.core.converters.models.FactConverterRequest;
import com.biit.factmanager.persistence.entities.Fact;
import com.biit.server.controller.converters.ElementConverter;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class FactConverter<ENTITY> extends ElementConverter<Fact<ENTITY>, FactDTO<ENTITY>, FactConverterRequest<ENTITY>> {

    @Override
    protected FactDTO<ENTITY> convertElement(FactConverterRequest<ENTITY> from) {
        final FactDTO<ENTITY> factDTO = new FactDTO<>();
        BeanUtils.copyProperties(from.getEntity(), factDTO);
        return factDTO;
    }

    @Override
    public Fact<ENTITY> reverse(FactDTO<ENTITY> to) {
        throw new UnsupportedOperationException();
    }
}
