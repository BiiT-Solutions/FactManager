package com.biit.factmanager.test;

import com.biit.factmanager.persistence.entities.Fact;
import com.biit.factmanager.persistence.repositories.FactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Collection;

@SpringBootTest
@Test(groups = "factRepository")
@Rollback(false)
public class FactRepositoryTests extends AbstractTransactionalTestNGSpringContextTests {

	private static final long FACT_PATIENT_ID = 1;
	private static final long FACT_COMPANY_ID = 1;
	private static final long FACT_ORGANIZATION_ID = 1;
	private static final String FACT_EXAMINATION_NAME = "test";

	@Autowired
	private FactRepository factRepository;

	private Fact fact = null;

	@Test
	private void getAllAtTheBeginning() {
		Assert.assertEquals(factRepository.count(), 0);
	}

	@Test(dependsOnMethods = "getAllAtTheBeginning")
	private void addFact() {
		Fact factTosave = new Fact();
		factTosave.setPatientId(FACT_PATIENT_ID);
		factTosave.setExaminationName(FACT_EXAMINATION_NAME);
		factTosave.setCompanyId(FACT_COMPANY_ID);
		factTosave.setOrganizationId(FACT_ORGANIZATION_ID);
		fact =  factRepository.save(factTosave);
		Assert.assertEquals(factRepository.count(), 1);
	}

	@Test(dependsOnMethods = "addFact")
	private void getFilteredFacts() {
		Assert.assertEquals(factRepository.count(), 1);
		// Filter by patientId and ExaminationName
		Collection<Fact> retrievedFacts =  factRepository.findByPatientIdAndExaminationName(FACT_PATIENT_ID,
				FACT_EXAMINATION_NAME);
		// Filter by companyId and ExaminationName
		retrievedFacts =  factRepository.findByCompanyIdAndExaminationName(FACT_COMPANY_ID,
				FACT_EXAMINATION_NAME);
		Assert.assertEquals(retrievedFacts.size(), 1);

		// Filter by organizationId and ExaminationName
		retrievedFacts =  factRepository.findByOrganizationIdAndExaminationName(FACT_ORGANIZATION_ID,
				FACT_EXAMINATION_NAME);
		Assert.assertEquals(retrievedFacts.size(), 1);
	}

	@Test(dependsOnMethods = "getFilteredFacts")
	private void deleteFact() {
		factRepository.delete(fact);
		Assert.assertEquals(factRepository.count(), 0);
	}
}
