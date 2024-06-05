package com.biit.factmanager.core.controllers;


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
            String organization, String createdBy, String application, String tenant, String session, String subject,
            String group, String element, String elementName, String factType, LocalDateTime startDate, LocalDateTime endDate,
            Integer lastDays, Boolean latestByUser, Boolean discriminatorValue, Map<String, String> customProperties, Pair<String, Object>[] pairs) {

        FactManagerLogger.debug(this.getClass(), "Searching facts with parameters: organization '{}', createdBy '{}', application '{}',"
                        + " tenant '{}', session '{}', subject '{}', group '{}', element '{}', elementName '{}', factType '{}', startDate '{}',"
                        + " endDate '{}', lastDays '{}', latestByUser '{}', discriminatorValue '{}', customProperties '{}',"
                        + " options '{}'.",
                organization, createdBy, application, tenant, session, subject, group, element, elementName, factType, startDate, endDate, lastDays, latestByUser,
                discriminatorValue, customProperties, pairs);
        final List<Fact<ENTITY>> facts = getProvider().findBy(organization, createdBy, application, tenant, session, subject, group, element, elementName,
                factType, startDate, endDate, lastDays, latestByUser, discriminatorValue, customProperties, pairs);

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
}
