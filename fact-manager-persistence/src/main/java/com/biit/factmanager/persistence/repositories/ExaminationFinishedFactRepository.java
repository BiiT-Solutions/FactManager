package com.biit.factmanager.persistence.repositories;

import com.biit.factmanager.persistence.entities.ExaminationFinishedFact;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Collection;

@Repository
@Transactional
public interface ExaminationFinishedFactRepository extends CrudRepository<ExaminationFinishedFact, Integer> {

    Collection<ExaminationFinishedFact> findByPatientId(long patientId);

    Collection<ExaminationFinishedFact> findByPatientIdAndExaminationName(long patientId, String examinationName);

    Collection<ExaminationFinishedFact> findByCompanyIdAndExaminationName(long companyId, String examinationName);

    Collection<ExaminationFinishedFact> findByOrganizationIdAndExaminationName(long organizationId, String examinationName);
}
