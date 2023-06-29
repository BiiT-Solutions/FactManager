package com.biit.factmanager.core.controllers;


import com.biit.factmanager.core.controllers.models.FactDTO;
import com.biit.factmanager.core.converters.FactConverter;
import com.biit.factmanager.core.converters.models.FactConverterRequest;
import com.biit.factmanager.core.providers.FactProvider;
import com.biit.factmanager.persistence.entities.Fact;
import com.biit.factmanager.persistence.repositories.FactRepository;
import com.biit.server.controller.BasicElementController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;

public abstract class FactController<ENTITY> extends BasicElementController<Fact<ENTITY>, FactDTO<ENTITY>, FactRepository<Fact<ENTITY>>,
        FactProvider<Fact<ENTITY>>, FactConverterRequest<ENTITY>, FactConverter<ENTITY>> {


    @Autowired
    protected FactController(FactProvider<Fact<ENTITY>> provider, FactConverter<ENTITY> converter) {
        super(provider, converter);
    }

    @Override
    protected FactConverterRequest<ENTITY> createConverterRequest(Fact<ENTITY> entity) {
        return new FactConverterRequest<>(entity);
    }

    public Collection<FactDTO<ENTITY>> findBy(
            String organization, String issuer, String application, String tenant, String tag,
            String group, String element, String process, LocalDateTime startDate, LocalDateTime endDate,
            Integer lastDays, Boolean discriminatorValue, Map<String, String> customProperties, Pair<String, Object>[] pairs) {

        return convertAll(getProvider().findBy(organization, issuer, application, tenant, tag, group, element, process,
                startDate, endDate, lastDays, discriminatorValue, customProperties, pairs));
    }
}
