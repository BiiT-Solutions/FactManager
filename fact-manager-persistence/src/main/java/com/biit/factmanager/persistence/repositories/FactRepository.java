package com.biit.factmanager.persistence.repositories;

import com.biit.factmanager.persistence.entities.Fact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface FactRepository<T extends Fact<?>> extends JpaRepository<T, Long>, CustomFactRepository<T> {

    List<T> findByElementIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThanEqual
            (String elementId, LocalDateTime startDate, LocalDateTime endDate);

    List<T> findByCreatedAtGreaterThanEqualAndCreatedAtLessThanEqual(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT f FROM Fact f WHERE (:organizationId is null or f.organizationId = :organizationId) " +
            "and (:tenantId is null or f.tenantId = :tenantId) " +
            "and (:tag is null or f.tag = :tag) " +
            "and (:group is null or f.group = :group)  " +
            "and (:elementId is null or f.elementId = :elementId) " +
            "and (:startDate is null or f.createdAt >= :startDate) " +
            "and (:endDate is null or f.createdAt <= :endDate)")
    List<T> findBy(@Param("organizationId") String organizationId, @Param("tenantId") String tenantId, @Param("tag") String tag,
                   @Param("group") String group, @Param("elementId") String elementId,
                   @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    List<T> findByCreatedAtLessThan(LocalDateTime createdAt);

    List<T> findByCreatedAtGreaterThan(LocalDateTime createdAt);

    @Query("SELECT f FROM Fact f WHERE (:elementId is null or f.elementId = :elementId) and (:group is null or f.group = :group)" +
            "and (:startDate is null or f.createdAt >= :startDate) and (:endDate is null or f.createdAt <= :endDate)")
    List<T> findByElementIdAndGroupAndCreatedAt(@Param("elementId") String elementId, @Param("group") String group,
                                                @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    List<T> findByElementId(String elementId);

    List<T> findByGroup(String group);

    @Query("SELECT f FROM Fact f WHERE (:elementId is null or f.elementId = : elementId) OR " +
            "(:group is null or f.group = : group)")
    List<T> findByElementIdAndGroup(String elementId, String group);

    @Query("SELECT f FROM FormrunnerQuestionFact f WHERE (:tenantId is null or f.tenantId = :tenantId) and (:group is null or f.group = :group)" +
            "and (:startDate is null or f.createdAt >= :startDate) and (:endDate is null or f.createdAt <= :endDate)")
    List<T> findByTenantIdAndGroupAndCreatedAt(@Param("tenantId") String tenantId, @Param("group") String group, @Param("startDate") LocalDateTime startDate,
                                               @Param("endDate") LocalDateTime endDate);

    @Query("SELECT f FROM  FormrunnerQuestionFact f WHERE (:tenantId is null or f.tenantId = :tenantId) and (:elementId is null or f.elementId = :elementId)" +
            "and (:startDate is null or f.createdAt >= :startDate) and (:endDate is null or f.createdAt <= :endDate)")
    List<T> findByTenantIdAndElementIdAndCreatedAt(@Param("tenantId") String tenantId, @Param("elementId") String elementId,
                                                   @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
