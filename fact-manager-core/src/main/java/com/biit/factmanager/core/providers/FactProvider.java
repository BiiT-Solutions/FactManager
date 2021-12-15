package com.biit.factmanager.core.providers;

import com.biit.factmanager.core.providers.exceptions.FactNotFoundException;
import com.biit.factmanager.core.providers.exceptions.InvalidParameterException;
import com.biit.factmanager.logger.FactManagerLogger;
import com.biit.factmanager.persistence.entities.Fact;
import com.biit.factmanager.persistence.repositories.FactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Service
public class FactProvider<T extends Fact<?>> {
    private final FactRepository<T> factRepository;


    @Autowired
    public FactProvider(FactRepository<T> factRepository) {
        this.factRepository = factRepository;
    }

    public T get(Long factId) {
        return factRepository.findById(factId).orElseThrow(
                () -> new FactNotFoundException(this.getClass(), "No fact with id '" + factId + "' found."));
    }

    public List<T> getAll() {
        return factRepository.findAll();
    }

    public Collection<T> getFiltered(String group, String elementId) {
        return factRepository.findByElementIdAndGroup(elementId, group);
    }

    public Collection<T> getByValueParameter(Object... pairParameterValues) {
        if (pairParameterValues.length % 2 == 1) {
            throw new InvalidParameterException(this.getClass(), "Parameters '" + Arrays.toString(pairParameterValues) + "' must be even.");
        }
        final Pair<String, Object>[] pairs = new Pair[pairParameterValues.length / 2];
        for (int i = 0; i < pairParameterValues.length; i += 2) {
            pairs[i] = Pair.of(pairParameterValues[i].toString(), pairParameterValues[i + 1]);
        }
        return factRepository.findByValueParameters(pairs);
    }

    public Collection<T> findBy(String organizationId, String tenantId, String tag, String group, String elementId, LocalDateTime startDate,
                                LocalDateTime endDate, Integer lastDays, Pair<String, Object>... valueParameters) {
        if (lastDays == null) {
            return findBy(organizationId, tenantId, tag, group, elementId, startDate, endDate, valueParameters);
        } else {
            return findBy(organizationId, tenantId, tag, group, elementId, lastDays, valueParameters);
        }
    }


    public Collection<T> findBy(String organizationId, String tenantId, String tag, String group, String elementId, Integer lastDays,
                                Pair<String, Object>... valueParameters) {
        final LocalDateTime startDate = LocalDateTime.now().minusDays(lastDays);
        final LocalDateTime endDate = LocalDateTime.now();
        return factRepository.findBy(organizationId, tenantId, tag, group, elementId, startDate, endDate, valueParameters);
    }

    public Collection<T> findBy(String organizationId, String tenantId, String tag, String group, String elementId, LocalDateTime startDate,
                                LocalDateTime endDate, Pair<String, Object>... valueParameters) {
        return factRepository.findBy(organizationId, tenantId, tag, group, elementId, startDate, endDate, valueParameters);
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
