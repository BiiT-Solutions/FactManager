package com.biit.factmanager.core.providers;

import com.biit.factmanager.core.providers.exceptions.FactNotFoundException;
import com.biit.factmanager.persistence.entities.Fact;
import com.biit.factmanager.persistence.repositories.FactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class FactProvider {
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


    public Fact add(Fact fact) {
        return factRepository.save(fact);
    }

    public Fact update(Fact fact) {
        return factRepository.save(fact);
    }

    public void delete(Fact fact) {
        factRepository.delete(fact);
    }

}
