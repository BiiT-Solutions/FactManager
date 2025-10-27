package com.biit.factmanager.core.converters.models;

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
import com.biit.server.converters.models.ConverterRequest;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class FactConverterRequest<ENTITY> extends ConverterRequest<Fact<ENTITY>> {

    private final Collection<CustomProperty> customProperties;

    public FactConverterRequest(Fact<ENTITY> entity) {
        super(entity);
        this.customProperties = null;
    }


    public FactConverterRequest(Fact<ENTITY> entity, Collection<CustomProperty> customProperties) {
        super(entity);
        this.customProperties = customProperties;
    }

    public Collection<CustomProperty> getCustomProperties() {
        return customProperties;
    }

    public static <T, V> Function<V, T> create(
            Supplier<? extends T> constructor, BiConsumer<? super T, ? super V> setter) {
        return v -> {
            final T t = constructor.get();
            setter.accept(t, v);
            return t;
        };
    }
}
