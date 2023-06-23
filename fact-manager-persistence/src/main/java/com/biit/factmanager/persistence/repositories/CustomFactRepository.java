package com.biit.factmanager.persistence.repositories;

import com.biit.factmanager.persistence.entities.Fact;
import org.springframework.data.util.Pair;

import java.time.LocalDateTime;
import java.util.List;

public interface CustomFactRepository<T extends Fact<?>> {

    List<T> findByValueParameters(Pair<String, Object>... valueParameters);

    List<T> findBy(Class<T> entityTypeClass, String organizationId, String issuer, String application, String tenantId, String tag,
                   String group, String elementId, String processId, LocalDateTime startDate, LocalDateTime endDate,
                   Boolean discriminatorValue, Pair<String, Object>... valueParameters);
}
