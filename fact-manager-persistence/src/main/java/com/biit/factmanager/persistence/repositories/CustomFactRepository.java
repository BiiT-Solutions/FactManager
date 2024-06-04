package com.biit.factmanager.persistence.repositories;

import com.biit.factmanager.persistence.entities.Fact;
import org.springframework.data.util.Pair;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface CustomFactRepository<T extends Fact<?>> {

    /**
     * Has any of these properties.
     *
     * @param customProperties
     * @return
     */
    List<T> findByCustomProperty(Map<String, String> customProperties);

    List<T> findByValueParameters(Pair<String, Object>... valueParameters);

    List<T> findBy(Class<T> entityTypeClass, String organization, String createdBy, String application, String tenant,
                   String group, String element, String elementName, String session, String subject, String factType,
                   LocalDateTime startDate, LocalDateTime endDate, Boolean latestByUser, Boolean discriminatorValue,
                   Map<String, String> customProperties, Pair<String, Object>... valueParameters);
}
