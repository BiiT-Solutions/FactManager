package com.biit.factmanager.core.providers;

import com.biit.factmanager.core.providers.exceptions.FactNotFoundException;
import com.biit.factmanager.logger.FactManagerLogger;
import com.biit.factmanager.persistence.entities.ExaminationFinishedFact;
import com.biit.factmanager.persistence.enums.Level;
import com.biit.factmanager.persistence.repositories.ExaminationFinishedFactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class ExaminationFinishedFactProvider {
    private final ExaminationFinishedFactRepository examinationFinishedFactRepository;


    @Autowired
    public ExaminationFinishedFactProvider(ExaminationFinishedFactRepository examinationFinishedFactRepository) {
        this.examinationFinishedFactRepository = examinationFinishedFactRepository;
    }

    public ExaminationFinishedFact get(int factId) {
        return examinationFinishedFactRepository.findById(factId).orElseThrow(
                () -> new FactNotFoundException(this.getClass(), "No fact with id '" + factId + "' found."));
    }


    public Collection<ExaminationFinishedFact> getAll() {
        return (Collection<ExaminationFinishedFact>) examinationFinishedFactRepository.findAll();
    }

    public Collection<ExaminationFinishedFact> getPatientLevel(long id, String examinationName) {
        return examinationFinishedFactRepository.findByPatientIdAndExaminationName(id, examinationName);
    }

    public Collection<ExaminationFinishedFact> getCompanyLevel(long id, String examinationName) {
        return examinationFinishedFactRepository.findByCompanyIdAndExaminationName(id, examinationName);
    }

    public Collection<ExaminationFinishedFact> getOrganizationLevel(long id, String examinationName) {
        return examinationFinishedFactRepository.findByOrganizationIdAndExaminationName(id, examinationName);
    }

    public Collection<ExaminationFinishedFact> getFiltered(Level level, Long id, String organizationName) {
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
    public ExaminationFinishedFact add(ExaminationFinishedFact fact) {
        return examinationFinishedFactRepository.save(fact);
    }

    /**
     * Saves the given entity List. Use the returned instance for further operations as the save operation might have changed the
     * entity instance completely.
     *
     * @param facts must not be {@literal null}.
     * @return the List of saved entitis; will never be {@literal null}.
     * @throws IllegalArgumentException in case the given {@literal entity} is {@literal null}.
     */
    public List<ExaminationFinishedFact> save(List<ExaminationFinishedFact> facts) {
        final List<ExaminationFinishedFact> savedFacts = new ArrayList<>();
        for (final ExaminationFinishedFact fact: facts) {
            final ExaminationFinishedFact savedFact = add(fact);
            savedFacts.add(savedFact);
            FactManagerLogger.debug(this.getClass().getName(), "Saved fact " + fact.toString());
        }
        return savedFacts;
    }

    public ExaminationFinishedFact update(ExaminationFinishedFact fact) {
        return examinationFinishedFactRepository.save(fact);
    }

    public void delete(ExaminationFinishedFact fact) {
        examinationFinishedFactRepository.delete(fact);
    }

    /**
     * Returns the number of entities available.
     *
     * @return the number of entities.
     */
    public long count() {
        return examinationFinishedFactRepository.count();
    }

}
