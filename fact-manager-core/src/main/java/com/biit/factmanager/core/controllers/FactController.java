package com.biit.factmanager.core.controllers;

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


import com.biit.factmanager.core.converters.FactConverter;
import com.biit.factmanager.core.converters.models.FactConverterRequest;
import com.biit.factmanager.core.providers.CustomPropertyProvider;
import com.biit.factmanager.core.providers.FactProvider;
import com.biit.factmanager.dto.FactDTO;
import com.biit.factmanager.logger.FactManagerLogger;
import com.biit.factmanager.persistence.entities.CustomProperty;
import com.biit.factmanager.persistence.entities.Fact;
import com.biit.factmanager.persistence.repositories.FactRepository;
import com.biit.server.controller.CrudController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class FactController<ENTITY> extends CrudController<Fact<ENTITY>, FactDTO, FactRepository<Fact<ENTITY>>,
        FactProvider<Fact<ENTITY>>, FactConverterRequest<ENTITY>, FactConverter<ENTITY>> {

    private final CustomPropertyProvider<Fact<ENTITY>> customPropertyProvider;

    @Autowired
    protected FactController(FactProvider<Fact<ENTITY>> provider, FactConverter<ENTITY> converter,
                             CustomPropertyProvider<Fact<ENTITY>> customPropertyProvider) {
        super(provider, converter);
        this.customPropertyProvider = customPropertyProvider;
    }

    @Override
    protected FactConverterRequest<ENTITY> createConverterRequest(Fact<ENTITY> entity) {
        return new FactConverterRequest<>(entity);
    }

    public Collection<FactDTO> findBy(
            String organization, String unit, Collection<String> createdBy, String application, String tenant, String session, String subject,
            String group, String element, String elementName, String factType, LocalDateTime startDate, LocalDateTime endDate,
            Integer lastDays, Boolean latestByUser, Boolean discriminatorValue, Map<String, String> customProperties, Pair<String, Object>[] pairs,
            int page, int size) {

        FactManagerLogger.debug(this.getClass(), "Searching facts with parameters: organization '{}', unit '{}', createdBy '{}', "
                        + "application '{}', tenant '{}', session '{}', subject '{}', group '{}', element '{}', elementName '{}', factType '{}', "
                        + "startDate '{}', endDate '{}', lastDays '{}', latestByUser '{}', discriminatorValue '{}', customProperties '{}',"
                        + " options '{}'.",
                organization, unit, createdBy, application, tenant, session, subject, group, element, elementName, factType, startDate, endDate,
                lastDays, latestByUser, discriminatorValue, customProperties, pairs);

        final List<Fact<ENTITY>> facts = getProvider().findBy(organization, unit, createdBy, application, tenant, session, subject, group, element, elementName,
                factType, startDate, endDate, lastDays, latestByUser, discriminatorValue, customProperties, page, size, pairs);

        final List<CustomProperty> factsCustomProperties = customPropertyProvider.findByFacts(facts);

        return convertAll(facts, factsCustomProperties);
    }

    protected List<FactDTO> convertAll(Collection<Fact<ENTITY>> entities, Collection<CustomProperty> customProperties) {
        final Map<Long, List<CustomProperty>> customPropertiesByFact = convert(customProperties);

        final List<FactDTO> factDTOS = new ArrayList<>();
        entities.forEach(entity -> factDTOS.add(getConverter().convert(new FactConverterRequest<>(entity, customPropertiesByFact.get(entity.getId())))));
        return factDTOS;
    }

    private Map<Long, List<CustomProperty>> convert(Collection<CustomProperty> customProperties) {
        final Map<Long, List<CustomProperty>> customPropertiesByFact = new HashMap<>();
        for (CustomProperty customProperty : customProperties) {
            customPropertiesByFact.computeIfAbsent(customProperty.getFact().getId(), fact -> new ArrayList<>()).add(customProperty);
        }
        return customPropertiesByFact;
    }

    public void updateBySession(String session, LocalDateTime creationTime, String updatedBy) {
        final List<Fact<ENTITY>> facts = getProvider().findBySession(session);
        if (facts.isEmpty()) {
            FactManagerLogger.warning(this.getClass(), "No facts at session '{}'", session);
            return;
        }
        boolean updated = false;
        if (creationTime != null) {
            updated = true;
            facts.forEach(fact -> fact.setCreatedAt(creationTime));
        }
        if (updated) {
            facts.forEach(fact -> {
                fact.setUpdatedBy(updatedBy);
                fact.setUpdatedAt(LocalDateTime.now());
            });
            getProvider().saveAll(facts);
        } else {
            FactManagerLogger.warning(this.getClass(), "Nothing to update at session '{}'", session);
        }
    }
}
