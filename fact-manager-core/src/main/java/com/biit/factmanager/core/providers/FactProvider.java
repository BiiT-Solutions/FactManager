package com.biit.factmanager.core.providers;

import com.biit.factmanager.core.providers.exceptions.FactNotFoundException;
import com.biit.factmanager.logger.FactManagerLogger;
import com.biit.factmanager.persistence.entities.Fact;
import com.biit.factmanager.persistence.entities.FormRunnerFact;
import com.biit.factmanager.persistence.repositories.FactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class FactProvider<E, T extends Fact<E>> {
    private final FactRepository<E, T> factRepository;


    @Autowired
    public FactProvider(FactRepository<E, T> factRepository) {
        this.factRepository = factRepository;
    }

    public T get(Long factId) {
        return factRepository.findById(factId).orElseThrow(
                () -> new FactNotFoundException(this.getClass(), "No fact with id '" + factId + "' found."));
    }

    public Collection<T> getAll() {
        return factRepository.findAll();
    }

    public Collection<T> getFiltered(String group, String elementId) {
        if (elementId != null && group != null) {
            return factRepository.findByElementIdAndGroup(elementId, group);
        } else if (group != null) {
            return factRepository.findByGroup(group);
        } else if (elementId != null) {
            return factRepository.findByElementId(elementId);
        }

        return getAll();
    }

    /**
     * Saves a given entity. Use the returned instance for further operations as the save operation might have changed the
     * entity instance completely.
     *
     * @param fact must not be {@literal null}.
     * @return the saved entity; will never be {@literal null}.
     * @throws IllegalArgumentException in case the given {@literal entity} is {@literal null}.
     */
    public T save(T fact) {
        return factRepository.save(fact);
    }

    /**
     * Saves the given entity List. Use the returned instance for further operations as the save operation might have changed the
     * entity instance completely.
     *
     * @param facts must not be {@literal null}.
     * @return the List of saved entitis; will never be {@literal null}.
     * @throws IllegalArgumentException in case the given {@literal entity} is {@literal null}.
     */
    public List<T> save(List<T> facts) {
        final List<T> savedFacts = new ArrayList<>();
        for (final T fact : facts) {
            final T savedFact = save(fact);
            savedFacts.add(savedFact);
            FactManagerLogger.debug(this.getClass().getName(), "Saved fact '{}'.", fact);
        }
        return savedFacts;
    }

    public T update(T fact) {
        return factRepository.save(fact);
    }

    public void delete(T fact) {
        factRepository.delete(fact);
    }

    /**
     * Returns the number of entities available.
     *
     * @return the number of entities.
     */
    public long count() {
        return factRepository.count();
    }

}
