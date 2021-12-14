package com.biit.factmanager.persistence.repositories;

import com.biit.factmanager.persistence.entities.Fact;
import org.springframework.data.util.Pair;

import java.util.Collection;

public interface CustomFactRepository<T extends Fact<?>> {

    Collection<T> findByValueParameters(Pair<String, Object>... valueParameters);
}
