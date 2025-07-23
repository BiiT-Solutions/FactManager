package com.biit.factmanager.persistence.repositories;

import com.biit.factmanager.persistence.entities.Fact;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface CustomFactRepository<T extends Fact<?>> {

    /**
     * Has any of these properties.
     *
     * @param customProperties
     * @return
     */
    List<T> findByCustomProperty(Map<String, String> customProperties, Pageable pageable);

    List<T> findByValueParameters(Pageable pageable, Pair<String, Object>... valueParameters);

    List<T> findBy(Class<T> entityTypeClass, String organization, String unit, Collection<String> createdBy, String application, String tenant, String group,
                   String element, String elementName, String session, String subject, String factType, LocalDateTime startDate, LocalDateTime endDate,
                   Boolean latestByUser, Boolean discriminatorValue, Map<String, String> customProperties, Pageable pageable,
                   Pair<String, Object>... valueParameters);
}
