package com.biit.factmanager.core.providers;

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
