package com.biit.factmanager.persistence.repositories;

/*-
 * #%L
 * FactManager (Persistence)
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
