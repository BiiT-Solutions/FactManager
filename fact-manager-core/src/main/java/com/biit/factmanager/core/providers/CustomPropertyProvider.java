package com.biit.factmanager.core.providers;

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

import com.biit.factmanager.persistence.entities.CustomProperty;
import com.biit.factmanager.persistence.entities.Fact;
import com.biit.factmanager.persistence.repositories.CustomPropertyRepository;
import com.biit.server.providers.CrudProvider;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class CustomPropertyProvider<T extends Fact<?>> extends CrudProvider<CustomProperty, Long, CustomPropertyRepository<T>> {

    public CustomPropertyProvider(CustomPropertyRepository repository) {
        super(repository);
    }

    public List<CustomProperty> findByFact(Fact<?> fact) {
        return getRepository().findByFact(fact);
    }

    public List<CustomProperty> findByFacts(Collection<T> facts) {
        return getRepository().findByFactIn(facts);
    }

}
