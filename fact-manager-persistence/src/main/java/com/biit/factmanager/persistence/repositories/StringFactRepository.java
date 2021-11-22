package com.biit.factmanager.persistence.repositories;

import com.biit.factmanager.persistence.entities.StringFact;
import com.biit.factmanager.persistence.entities.values.StringValue;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface StringFactRepository extends FactRepository<StringValue, StringFact> {
}
