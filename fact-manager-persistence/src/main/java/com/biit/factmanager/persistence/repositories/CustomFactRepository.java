package com.biit.factmanager.persistence.repositories;

import com.biit.factmanager.persistence.entities.Fact;
import org.springframework.data.util.Pair;

import java.time.LocalDateTime;
import java.util.Collection;

public interface CustomFactRepository<T extends Fact<?>> {

    Collection<T> findByValueParameters(Pair<String, Object>... valueParameters);

    Collection<T> findBy(String organizationId, String tenantId, String tag, String group, String elementId, LocalDateTime startDate, LocalDateTime endDate,
                         Pair<String, Object>... valueParameters);
}
