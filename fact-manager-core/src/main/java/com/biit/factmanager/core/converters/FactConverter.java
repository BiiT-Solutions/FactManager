package com.biit.factmanager.core.converters;


import com.biit.factmanager.core.converters.models.CustomPropertyConverterRequest;
import com.biit.factmanager.core.converters.models.FactConverterRequest;
import com.biit.factmanager.core.providers.CustomPropertyProvider;
import com.biit.factmanager.dto.CustomPropertyDTO;
import com.biit.factmanager.dto.FactDTO;
import com.biit.factmanager.persistence.entities.Fact;
import com.biit.server.controller.converters.ElementConverter;
import org.hibernate.LazyInitializationException;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.stream.Collectors;

@Component
public class FactConverter<ENTITY> extends ElementConverter<Fact<ENTITY>, FactDTO, FactConverterRequest<ENTITY>> {

    private final CustomPropertyConverter customPropertyConverter;
    private final CustomPropertyProvider<Fact<ENTITY>> customPropertyProvider;

    public FactConverter(CustomPropertyConverter customPropertyConverter,
                         CustomPropertyProvider<Fact<ENTITY>> customPropertyProvider) {
        this.customPropertyConverter = customPropertyConverter;
        this.customPropertyProvider = customPropertyProvider;
    }

    @Override
    protected FactDTO convertElement(FactConverterRequest<ENTITY> from) {
        final FactDTO factDTO = new FactDTO();
        BeanUtils.copyProperties(from.getEntity(), factDTO);
        Collection<CustomPropertyDTO> customProperties;
        try {
            if (from.getCustomProperties() != null) {
                customProperties = customPropertyConverter.convertAll(from.getCustomProperties().stream()
                        .map(CustomPropertyConverterRequest::new).collect(Collectors.toList()));
            } else {
                customProperties = customPropertyConverter.convertAll(from.getEntity().getCustomProperties().stream()
                        .map(CustomPropertyConverterRequest::new).collect(Collectors.toList()));
            }
        } catch (LazyInitializationException e) {
            customProperties = customPropertyConverter.convertAll(customPropertyProvider.findByFact(from.getEntity()).stream()
                    .map(CustomPropertyConverterRequest::new).collect(Collectors.toList()));
        }

        factDTO.setCustomProperties(customProperties);
        return factDTO;
    }

    @Override
    public Fact<ENTITY> reverse(FactDTO to) {
        throw new UnsupportedOperationException();
    }
}
