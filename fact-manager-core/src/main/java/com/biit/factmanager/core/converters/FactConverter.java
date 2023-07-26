package com.biit.factmanager.core.converters;


import com.biit.factmanager.core.converters.models.CustomPropertyConverterRequest;
import com.biit.factmanager.core.converters.models.FactConverterRequest;
import com.biit.factmanager.dto.FactDTO;
import com.biit.factmanager.persistence.entities.CustomProperty;
import com.biit.factmanager.persistence.entities.Fact;
import com.biit.factmanager.persistence.repositories.CustomPropertyRepository;
import com.biit.server.controller.converters.ElementConverter;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class FactConverter<ENTITY> extends ElementConverter<Fact<ENTITY>, FactDTO, FactConverterRequest<ENTITY>> {

    private final CustomPropertyConverter customPropertyConverter;
    private final CustomPropertyRepository customPropertyRepository;

    public FactConverter(CustomPropertyConverter customPropertyConverter, CustomPropertyRepository customPropertyRepository) {
        this.customPropertyConverter = customPropertyConverter;
        this.customPropertyRepository = customPropertyRepository;
    }

    @Override
    protected FactDTO convertElement(FactConverterRequest<ENTITY> from) {
        final FactDTO factDTO = new FactDTO();
        BeanUtils.copyProperties(from.getEntity(), factDTO);
        final List<CustomProperty> customProperties = customPropertyRepository.findByFact(from.getEntity());
        factDTO.setCustomProperties(customPropertyConverter.convertAll(customProperties.stream()
                .map(CustomPropertyConverterRequest::new).collect(Collectors.toList())));
        return factDTO;
    }

    @Override
    public Fact<ENTITY> reverse(FactDTO to) {
        throw new UnsupportedOperationException();
    }
}
