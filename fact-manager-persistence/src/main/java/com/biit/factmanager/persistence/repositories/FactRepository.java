package com.biit.factmanager.persistence.repositories;

import com.biit.factmanager.persistence.entities.Fact;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.Collection;

public interface FactRepository<T> extends CrudRepository<Fact<T>, Integer> {

    Collection<Fact<T>> findByTenantIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThanEqual
            (long tenantId, LocalDateTime startDate, LocalDateTime endDate);

    Collection<Fact<T>> findByElementIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThanEqual
            (String elementId, LocalDateTime startDate, LocalDateTime endDate);


    Collection<Fact<T>> findByCategoryAndCreatedAtGreaterThanEqualAndCreatedAtLessThanEqual
            (String category, LocalDateTime startDate, LocalDateTime endDate);

    Collection<Fact<T>> findByTenantIdAndCategory(long tenantId, String category);

    Collection<Fact<T>> findByTenantIdAndElementIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThanEqual
            (long tenantId, String elementId, LocalDateTime startDate, LocalDateTime endDate);

    Collection<Fact<T>> findByTenantIdAndCategoryAndCreatedAtGreaterThanEqualAndCreatedAtLessThanEqual
            (long tenantId, String category, LocalDateTime startDate, LocalDateTime endDate);

    Collection<Fact<T>> findByCreatedAtGreaterThanEqualAndCreatedAtLessThanEqual(LocalDateTime startDate, LocalDateTime endDate);

    Collection<Fact<T>> findByCreatedAtLessThan(LocalDateTime createdAt);

    Collection<Fact<T>> findByCreatedAtGreaterThan(LocalDateTime createdAt);
}
