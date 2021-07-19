package com.biit.factmanager.core.providers;

import com.biit.factmanager.core.providers.exceptions.FactNotFoundException;
import com.biit.factmanager.logger.FactManagerLogger;
import com.biit.factmanager.persistence.entities.Fact;
import com.biit.factmanager.persistence.enums.Level;
import com.biit.factmanager.persistence.repositories.FactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class FormrunnerFactProvider {
    private final FactRepository factRepository;


    @Autowired
    public FactProvider(FactRepository factRepository) {
        this.factRepository = factRepository;
    }

    public Fact get(int factId) {
        return factRepository.findById(factId).orElseThrow(
                () -> new FactNotFoundException(this.getClass(), "No fact with id '" + factId + "' found."));
    }


    public Collection<Fact> getAll() {
        return StreamSupport.stream(factRepository.findAll().spliterator(), false).collect(Collectors.toSet());
    }

    public Collection<Fact> getPatientLevel(long id, String examinationName) {
        return StreamSupport.stream(factRepository.findByPatientIdAndExaminationName(id, examinationName).spliterator(),
                false).collect(Collectors.toSet());
    }

    public Collection<Fact> getCompanyLevel(long id, String examinationName) {
        return StreamSupport.stream(factRepository.findByCompanyIdAndExaminationName(id, examinationName).spliterator(),
                false).collect(Collectors.toSet());
    }

    public Collection<Fact> getOrganizationLevel(long id, String examinationName) {
        return StreamSupport.stream(factRepository.findByOrganizationIdAndExaminationName(id, examinationName).spliterator(),
                false).collect(Collectors.toSet());
    }

    public Collection<Fact> getFiltered(Level level, Long id, String organizationName) {
        if (level != null && id != null) {
            switch (level) {
                case PATIENT:
                    return getPatientLevel(id, organizationName);
                case COMPANY:
                    return getCompanyLevel(id, organizationName);
                case ORGANIZATION:
                    return getOrganizationLevel(id, organizationName);
                default:
                    return getAll();
            }
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
    public Fact add(Fact fact) {
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
    public List<Fact> save(List<Fact> facts) {
        final List<Fact> savedFacts = new ArrayList<>();
        for (final Fact fact: facts) {
            final Fact savedFact = add(fact);
            savedFacts.add(savedFact);
            FactManagerLogger.debug(this.getClass().getName(), "Saved fact " + fact.toString());
        }
        return savedFacts;
    }

    public Fact update(Fact fact) {
        return factRepository.save(fact);
    }

    public void delete(Fact fact) {
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
