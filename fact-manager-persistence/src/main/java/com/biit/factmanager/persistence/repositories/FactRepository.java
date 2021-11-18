package com.biit.factmanager.persistence.repositories;

import com.biit.factmanager.persistence.entities.Fact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Collection;

public interface FactRepository<E, T extends Fact<E>> extends JpaRepository<T, Integer> {

    Collection<T> findByTenantIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThanEqual
            (long tenantId, LocalDateTime startDate, LocalDateTime endDate);

    Collection<T> findByElementIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThanEqual
            (String elementId, LocalDateTime startDate, LocalDateTime endDate);


    Collection<T> findByCategoryAndCreatedAtGreaterThanEqualAndCreatedAtLessThanEqual
            (String category, LocalDateTime startDate, LocalDateTime endDate);

    Collection<T> findByTenantIdAndCategory(long tenantId, String category);

    Collection<T> findByTenantIdAndElementIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThanEqual
            (long tenantId, String elementId, LocalDateTime startDate, LocalDateTime endDate);

    Collection<T> findByTenantIdAndCategoryAndCreatedAtGreaterThanEqualAndCreatedAtLessThanEqual
            (long tenantId, String category, LocalDateTime startDate, LocalDateTime endDate);

    Collection<T> findByCreatedAtGreaterThanEqualAndCreatedAtLessThanEqual(LocalDateTime startDate, LocalDateTime endDate);

    Collection<T> findByCreatedAtLessThan(LocalDateTime createdAt);

    Collection<T> findByCreatedAtGreaterThan(LocalDateTime createdAt);

    @Query("SELECT f FROM Fact f WHERE (:elementId is null or f.elementId = :elementId) and (:category is null or f.category = :category)" +
            "and (:startDate is null or f.createdAt >= :startDate) and (:endDate is null or f.createdAt <= :endDate)")
    Collection<T> findByElementIdAndCategoryAndCreatedAt(String elementId, String category, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT f FROM Fact f WHERE (:tenantId is null or f.tenantId = :tenantId) and (:category is null or f.category = :category)" +
            "and (:elementId is null or f.elementId = :elementId)" + "and (:startDate is null or f.createdAt >= :startDate) and (:endDate is null or " +
            "f.createdAt <= :endDate)")
    Collection<T> findByTenantIdAndCategoryAndElementIdAndCreatedAt(Long tenantId, String category, String elementId, LocalDateTime startDate,
                                                                    LocalDateTime endDate);

    Collection<T> findByElementId(String elementId);

    Collection<T> findByCategory(String category);

    Collection<T> findByElementIdAndCategory(String elementId, String category);

    Collection<T> findByTenantId(Long tenantId);

    Collection<T> findByTenantIdAndElementId(Long tenantId, String elementId);

    Collection<T> findByTenantIdAndCategoryAndElementId(Long tenantId, String category, String elementId);
}
