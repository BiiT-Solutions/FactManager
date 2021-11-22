package com.biit.factmanager.persistence.repositories;

import com.biit.factmanager.persistence.entities.values.FormRunnerValue;
import com.biit.factmanager.persistence.entities.FormRunnerFact;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collection;

@Repository
@Transactional
public interface FormRunnerFactRepository extends FactRepository<FormRunnerValue, FormRunnerFact> {
    @Query("SELECT f FROM FormRunnerFact f WHERE (:tenantId is null or f.tenantId = :tenantId) and (:group is null or f.group = :group)" +
            "and (:startDate is null or f.createdAt >= :startDate) and (:endDate is null or f.createdAt <= :endDate)")
    Collection<FormRunnerFact> findByTenantIdAndGroupAndCreatedAt(String tenantId, String group, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT f FROM  FormRunnerFact f WHERE (:tenantId is null or f.tenantId = :tenantId) and (:elementId is null or f.elementId = :elementId)" +
            "and (:startDate is null or f.createdAt >= :startDate) and (:endDate is null or f.createdAt <= :endDate)")
    Collection<FormRunnerFact> findByTenantIdAndElementIdAndCreatedAt(String tenantId, String elementId, LocalDateTime startDate, LocalDateTime endDate);

}
