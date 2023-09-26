package com.biit.factmanager.core.converters.models;

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
