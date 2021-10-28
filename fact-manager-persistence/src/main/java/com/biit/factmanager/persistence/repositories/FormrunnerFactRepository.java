package com.biit.factmanager.persistence.repositories;

import com.biit.factmanager.persistence.entities.FormrunnerFact;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collection;

@Repository
@Transactional
public interface FormrunnerFactRepository extends CrudRepository<FormrunnerFact, Integer> {

    Collection<FormrunnerFact> findByTenantIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThanEqual
            (long tenantId, LocalDateTime startDate, LocalDateTime endDate);

    Collection<FormrunnerFact> findByElementIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThanEqual
            (String elementId, LocalDateTime startDate, LocalDateTime endDate);


    Collection<FormrunnerFact> findByCategoryAndCreatedAtGreaterThanEqualAndCreatedAtLessThanEqual(String category, LocalDateTime startDate, LocalDateTime endDate);

    Collection<FormrunnerFact> findByTenantIdAndCategory(long tenantId, String category);

    Collection<FormrunnerFact> findByTenantIdAndElementIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThanEqual
            (long tenantId, String elementId, LocalDateTime startDate, LocalDateTime endDate);

    Collection<FormrunnerFact> findByTenantIdAndCategoryAndCreatedAtGreaterThanEqualAndCreatedAtLessThanEqual
            (long tenantId, String category, LocalDateTime startDate, LocalDateTime endDate);

    Collection<FormrunnerFact> findByCreatedAtGreaterThanEqualAndCreatedAtLessThanEqual(LocalDateTime startDate, LocalDateTime endDate);

    Collection<FormrunnerFact> findByCreatedAtLessThan(LocalDateTime createdAt);

    Collection<FormrunnerFact> findByCreatedAtGreaterThan(LocalDateTime createdAt);

    @Query("SELECT f FROM FormrunnerFact f WHERE (:tenantId is null or f.tenantId = :tenantId) and (:category is null or f.category = :category)" +
            "and (:startDate is null or f.createdAt >= :startDate) and (:endDate is null or f.createdAt <= :endDate)")
    Collection<FormrunnerFact> findByTenantIdAndCategoryAndCreatedAt(Long tenantId, String category, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT f FROM  FormrunnerFact f WHERE (:tenantId is null or f.tenantId = :tenantId) and (:elementId is null or f.elementId = :elementId)" +
            "and (:startDate is null or f.createdAt >= :startDate) and (:endDate is null or f.createdAt <= :endDate)")
    Collection<FormrunnerFact> findByTenantIdAndElementIdAndCreatedAt(Long tenantId, String elementId, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT f FROM FormrunnerFact f WHERE (:elementId is null or f.elementId = :elementId) and (:category is null or f.category = :category)" +
            "and (:startDate is null or f.createdAt >= :startDate) and (:endDate is null or f.createdAt <= :endDate)")
    Collection<FormrunnerFact> findByElementIdAndCategoryAndCreatedAt(String elementId, String category, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT f FROM FormrunnerFact f WHERE (:tenantId is null or f.tenantId = :tenantId) and (:category is null or f.category = :category)" +
            "and (:elementId is null or f.elementId = :elementId)" + "and (:startDate is null or f.createdAt >= :startDate) and (:endDate is null or " +
            "f.createdAt <= :endDate)")
    Collection<FormrunnerFact> findByTenantIdAndCategoryAndElementIdAndCreatedAt(Long tenantId, String category, String elementId, LocalDateTime startDate,
                                                                                 LocalDateTime endDate);
}
