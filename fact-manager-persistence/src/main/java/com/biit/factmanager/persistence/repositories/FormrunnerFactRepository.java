package com.biit.factmanager.persistence.repositories;

import com.biit.factmanager.persistence.entities.FormrunnerFact;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;

@Repository
@Transactional
public interface FormrunnerFactRepository extends CrudRepository<FormrunnerFact, Integer> {

    Collection<FormrunnerFact> findByPatientId(long patientId);

    Collection<FormrunnerFact> findByPatientIdAndExaminationName(long patientId, String examinationName);

    Collection<FormrunnerFact> findByCompanyIdAndExaminationName(long companyId, String examinationName);

    Collection<FormrunnerFact> findByOrganizationIdAndExaminationName(long organizationId, String examinationName);

    Collection<FormrunnerFact> findByCreatedAtGreaterThanEqualAndCreatedAtLessThanEqual(LocalDateTime startDate, LocalDateTime endDate);

    Collection<FormrunnerFact> findByCreatedAtLessThan(LocalDateTime createdAt);

    Collection<FormrunnerFact> findByCreatedAtGreaterThan(LocalDateTime createdAt);
}
