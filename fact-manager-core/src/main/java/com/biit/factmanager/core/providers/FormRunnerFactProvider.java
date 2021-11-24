package com.biit.factmanager.core.providers;

import com.biit.factmanager.core.providers.exceptions.FactNotFoundException;
import com.biit.factmanager.logger.FactManagerLogger;
import com.biit.factmanager.persistence.entities.FormRunnerFact;
import com.biit.factmanager.persistence.repositories.FormRunnerFactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FormRunnerFactProvider {
    private final FormRunnerFactRepository formRunnerFactRepository;


    @Autowired
    public FormRunnerFactProvider(FormRunnerFactRepository formRunnerFactRepository) {
        this.formRunnerFactRepository = formRunnerFactRepository;
    }

    public FormRunnerFact get(Long factId) {
        return formRunnerFactRepository.findById(factId).orElseThrow(
                () -> new FactNotFoundException(this.getClass(), "No fact with id '" + factId + "' found."));
    }

    public Collection<FormRunnerFact> getAll() {
        return formRunnerFactRepository.findAll();
    }

    public Collection<FormRunnerFact> getFiltered(String group, String elementId) {
        if (elementId != null && group != null) {
            return formRunnerFactRepository.findByElementIdAndGroup(elementId, group);
        } else if (group != null) {
            return formRunnerFactRepository.findByGroup(group);
        } else if (elementId != null) {
            return formRunnerFactRepository.findByElementId(elementId);
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
    public FormRunnerFact add(FormRunnerFact fact) {
        return formRunnerFactRepository.save(fact);
    }

    /**
     * Saves the given entity List. Use the returned instance for further operations as the save operation might have changed the
     * entity instance completely.
     *
     * @param facts must not be {@literal null}.
     * @return the List of saved entitis; will never be {@literal null}.
     * @throws IllegalArgumentException in case the given {@literal entity} is {@literal null}.
     */
    public List<FormRunnerFact> save(List<FormRunnerFact> facts) {
        final List<FormRunnerFact> savedFacts = new ArrayList<>();
        for (final FormRunnerFact fact : facts) {
            final FormRunnerFact savedFact = add(fact);
            savedFacts.add(savedFact);
            FactManagerLogger.debug(this.getClass().getName(), "Saved fact '{}'.", fact);
        }
        return savedFacts;
    }

    public FormRunnerFact update(FormRunnerFact fact) {
        return formRunnerFactRepository.save(fact);
    }

    public void delete(FormRunnerFact fact) {
        formRunnerFactRepository.delete(fact);
    }

    /**
     * Returns the number of entities available.
     *
     * @return the number of entities.
     */
    public long count() {
        return formRunnerFactRepository.count();
    }

}
