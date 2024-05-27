package com.biit.factmanager.core.converters;

import com.biit.factmanager.core.converters.models.CustomPropertyConverterRequest;
import com.biit.factmanager.dto.CustomPropertyDTO;
import com.biit.factmanager.persistence.entities.CustomProperty;
import com.biit.server.controller.converters.SimpleConverter;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class CustomPropertyConverter extends SimpleConverter<CustomProperty, CustomPropertyDTO, CustomPropertyConverterRequest> {


    @Override
    protected CustomPropertyDTO convertElement(CustomPropertyConverterRequest from) {
        final CustomPropertyDTO customPropertyDTO = new CustomPropertyDTO();
        BeanUtils.copyProperties(from.getEntity(), customPropertyDTO);
        return customPropertyDTO;
    }

    @Override
    public CustomProperty reverse(CustomPropertyDTO to) {
        final CustomProperty customProperty = new CustomProperty();
        BeanUtils.copyProperties(to, customProperty);
        return customProperty;
    }
}
