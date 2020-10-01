package com.biit.factmanager.persistence.repositories;

import com.biit.factmanager.persistence.entities.Fact;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Collection;

@Repository
@Transactional
public interface FactRepository extends CrudRepository<Fact, Integer> {


}
