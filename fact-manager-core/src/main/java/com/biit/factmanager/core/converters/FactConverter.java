package com.biit.factmanager.core.converters;


import com.biit.factmanager.core.converters.models.FactConverterRequest;
import com.biit.factmanager.dto.FactDTO;
import com.biit.factmanager.persistence.entities.Fact;
import com.biit.server.controller.converters.ElementConverter;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class FactConverter<ENTITY> extends ElementConverter<Fact<ENTITY>, FactDTO, FactConverterRequest<ENTITY>> {

    @Override
    protected FactDTO convertElement(FactConverterRequest<ENTITY> from) {
        final FactDTO factDTO = new FactDTO();
        BeanUtils.copyProperties(from.getEntity(), factDTO);
        return factDTO;
    }

    @Override
    public Fact<ENTITY> reverse(FactDTO to) {
        throw new UnsupportedOperationException();
    }
}
