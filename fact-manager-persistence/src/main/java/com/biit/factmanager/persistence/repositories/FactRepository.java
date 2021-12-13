package com.biit.factmanager.persistence.repositories;

import com.biit.factmanager.persistence.entities.Fact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Collection;

public interface FactRepository<T extends Fact<?>> extends JpaRepository<T, Long> {

    Collection<T> findByElementIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThanEqual
            (String elementId, LocalDateTime startDate, LocalDateTime endDate);

    Collection<T> findByCreatedAtGreaterThanEqualAndCreatedAtLessThanEqual(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT f FROM Fact f WHERE (:organizationId is null or f.organizationId = :organizationId) " +
            "and (:tenantId is null or f.tenantId = :tenantId) " +
            "and (:tag is null or f.tag = :tag) " +
            "and (:group is null or f.group = :group)  " +
            "and (:elementId is null or f.elementId = :elementId) " +
            "and (:startDate is null or f.createdAt >= :startDate) a" +
            "nd (:endDate is null or f.createdAt <= :endDate)")
    Collection<T> findBy(String organizationId, String tenantId, String tag, String group, String elementId, LocalDateTime startDate, LocalDateTime endDate);

    Collection<T> findByCreatedAtLessThan(LocalDateTime createdAt);

    Collection<T> findByCreatedAtGreaterThan(LocalDateTime createdAt);

    @Query("SELECT f FROM Fact f WHERE (:elementId is null or f.elementId = :elementId) and (:group is null or f.group = :group)" +
            "and (:startDate is null or f.createdAt >= :startDate) and (:endDate is null or f.createdAt <= :endDate)")
    Collection<T> findByElementIdAndGroupAndCreatedAt(String elementId, String group, LocalDateTime startDate, LocalDateTime endDate);

    Collection<T> findByElementId(String elementId);

    Collection<T> findByGroup(String group);

    Collection<T> findByElementIdAndGroup(String elementId, String group);

    @Query("SELECT f FROM FormRunnerFact f WHERE (:tenantId is null or f.tenantId = :tenantId) and (:group is null or f.group = :group)" +
            "and (:startDate is null or f.createdAt >= :startDate) and (:endDate is null or f.createdAt <= :endDate)")
    Collection<T> findByTenantIdAndGroupAndCreatedAt(String tenantId, String group, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT f FROM  FormRunnerFact f WHERE (:tenantId is null or f.tenantId = :tenantId) and (:elementId is null or f.elementId = :elementId)" +
            "and (:startDate is null or f.createdAt >= :startDate) and (:endDate is null or f.createdAt <= :endDate)")
    Collection<T> findByTenantIdAndElementIdAndCreatedAt(String tenantId, String elementId, LocalDateTime startDate, LocalDateTime endDate);


    @Query("SELECT f FROM Fact f WHERE f.value LIKE %?1%")
    Collection<T> findByValueParameter(String value);
}
