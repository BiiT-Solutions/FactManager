package com.biit.factmanager.persistence.repositories;

import com.biit.factmanager.persistence.entities.Fact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface FactRepository<T extends Fact<?>> extends JpaRepository<T, Long>, CustomFactRepository<T> {

    List<T> findByElementAndCreatedAtGreaterThanEqualAndCreatedAtLessThanEqual(
            String elementId, LocalDateTime startDate, LocalDateTime endDate);

    List<T> findByCreatedAtGreaterThanEqualAndCreatedAtLessThanEqual(LocalDateTime startDate, LocalDateTime endDate);

    @Query("""
              SELECT f FROM Fact f WHERE (:organization is null or f.organization = :organization)
              and (:customer is null or f.tenant = :customer)
              and (:application is null or f.tenant = :application)
              and (:tenant is null or f.tenant = :tenant)
              and (:tag is null or f.tag = :tag)
              and (:group is null or f.group = :group)
              and (:element is null or f.element = :element)
              and (:process is null or f.process = :process)
              and (:startDate is null or f.createdAt >= :startDate)
              and (:endDate is null or f.createdAt <= :endDate)
            """)
    List<T> findBy(@Param("organization") String organization, @Param("customer") String customer, @Param("application") String application,
                   @Param("tenant") String tenant, @Param("tag") String tag, @Param("group") String group,
                   @Param("element") String element, @Param("process") String process,
                   @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    List<T> findByCreatedAtLessThan(LocalDateTime createdAt);

    List<T> findByCreatedAtGreaterThan(LocalDateTime createdAt);

    @Query("SELECT f FROM Fact f WHERE (:element is null or f.element = :element) and (:group is null or f.group = :group)"
            + "and (:startDate is null or f.createdAt >= :startDate) and (:endDate is null or f.createdAt <= :endDate)")
    List<T> findByElementAndGroupAndCreatedAt(@Param("element") String element, @Param("group") String group,
                                              @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    List<T> findByElement(String elementId);

    List<T> findByGroup(String group);

    @Query("SELECT f FROM Fact f WHERE (:element is null or f.element = : element) OR "
            + "(:group is null or f.group = : group)")
    List<T> findByElementAndGroup(@Param("element") String element, @Param("group") String group);

    @Query("SELECT f FROM FormrunnerQuestionFact f WHERE (:tenant is null or f.tenant = :tenant) and (:group is null or f.group = :group)"
            + "and (:startDate is null or f.createdAt >= :startDate) and (:endDate is null or f.createdAt <= :endDate)")
    List<T> findByTenantAndGroupAndCreatedAt(@Param("tenant") String tenant, @Param("group") String group, @Param("startDate") LocalDateTime startDate,
                                             @Param("endDate") LocalDateTime endDate);

    @Query("SELECT f FROM  FormrunnerQuestionFact f WHERE (:tenant is null or f.tenant = :tenant) and (:element is null or f.element = :element)"
            + "and (:startDate is null or f.createdAt >= :startDate) and (:endDate is null or f.createdAt <= :endDate)")
    List<T> findByTenantIdAndElementAndCreatedAt(@Param("tenant") String tenant, @Param("element") String element,
                                                 @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    List<T> findByOrganization(@Param("organization") String organization);
}
