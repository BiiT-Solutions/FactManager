package com.biit.factmanager.core.converters.models;

import com.biit.factmanager.persistence.entities.CustomProperty;
import com.biit.server.converters.models.ConverterRequest;

public class CustomPropertyConverterRequest extends ConverterRequest<CustomProperty> {
    public CustomPropertyConverterRequest(CustomProperty entity) {
        super(entity);
    }
}
