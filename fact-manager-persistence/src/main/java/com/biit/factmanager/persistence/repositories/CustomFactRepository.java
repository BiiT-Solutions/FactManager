package com.biit.factmanager.persistence.repositories;

import com.biit.factmanager.persistence.entities.Fact;
import org.springframework.data.util.Pair;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface CustomFactRepository<T extends Fact<?>> {

    List<T> findByCustomProperties(Map<String, String> customProperties);

    List<T> findByValueParameters(Pair<String, Object>... valueParameters);

    List<T> findBy(Class<T> entityTypeClass, String organizationId, String issuer, String application, String tenantId, String tag,
                   String group, String elementId, String processId, LocalDateTime startDate, LocalDateTime endDate,
                   Boolean discriminatorValue, Map<String, String> customProperties, Pair<String, Object>... valueParameters);
}
