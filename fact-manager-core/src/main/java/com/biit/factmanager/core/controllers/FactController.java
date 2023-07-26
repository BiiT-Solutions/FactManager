package com.biit.factmanager.core.controllers;


import com.biit.factmanager.core.converters.FactConverter;
import com.biit.factmanager.core.converters.models.FactConverterRequest;
import com.biit.factmanager.core.providers.FactProvider;
import com.biit.factmanager.dto.FactDTO;
import com.biit.factmanager.persistence.entities.Fact;
import com.biit.factmanager.persistence.repositories.FactRepository;
import com.biit.server.controller.BasicElementController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;

@Controller
public class FactController<ENTITY> extends BasicElementController<Fact<ENTITY>, FactDTO, FactRepository<Fact<ENTITY>>,
        FactProvider<Fact<ENTITY>>, FactConverterRequest<ENTITY>, FactConverter<ENTITY>> {


    @Autowired
    protected FactController(FactProvider<Fact<ENTITY>> provider, FactConverter<ENTITY> converter) {
        super(provider, converter);
    }

    @Override
    protected FactConverterRequest<ENTITY> createConverterRequest(Fact<ENTITY> entity) {
        return new FactConverterRequest<>(entity);
    }

    public Collection<FactDTO> findBy(
            String organization, String customer, String application, String tenant, String session, String subject,
            String group, String element, String factType, String valueType, LocalDateTime startDate, LocalDateTime endDate,
            Integer lastDays, Boolean discriminatorValue, Map<String, String> customProperties, Pair<String, Object>[] pairs) {

        return convertAll(getProvider().findBy(organization, customer, application, tenant, session, subject, group, element, factType,
                valueType, startDate, endDate, lastDays, discriminatorValue, customProperties, pairs));
    }
}
