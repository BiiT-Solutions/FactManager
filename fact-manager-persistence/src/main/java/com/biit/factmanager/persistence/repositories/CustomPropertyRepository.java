package com.biit.factmanager.persistence.repositories;

import com.biit.factmanager.persistence.entities.CustomProperty;
import com.biit.factmanager.persistence.entities.Fact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
public interface CustomPropertyRepository extends JpaRepository<CustomProperty, Long> {

        List<CustomProperty> findByFact(Fact<?> fact);
}
