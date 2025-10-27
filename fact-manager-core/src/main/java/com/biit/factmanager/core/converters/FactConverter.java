package com.biit.factmanager.core.converters;

/*-
 * #%L
 * FactManager (core)
 * %%
 * Copyright (C) 2020 - 2025 BiiT Sourcing Solutions S.L.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */


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
