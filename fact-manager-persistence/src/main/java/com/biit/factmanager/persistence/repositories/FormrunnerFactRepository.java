package com.biit.factmanager.persistence.repositories;

import com.biit.factmanager.persistence.entities.Fact;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Collection;

@Repository
@Transactional
public interface FormrunnerFactRepository extends CrudRepository<Fact, Integer> {

    Collection<Fact> findByPatientId(long patientId);

    Collection<Fact> findByPatientIdAndExaminationName(long patientId, String examinationName);

    Collection<Fact> findByCompanyIdAndExaminationName(long companyId, String examinationName);

    Collection<Fact> findByOrganizationIdAndExaminationName(long organizationId, String examinationName);
}
