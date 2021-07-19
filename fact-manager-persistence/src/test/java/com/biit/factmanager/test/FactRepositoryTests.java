package com.biit.factmanager.test;

import com.biit.factmanager.persistence.entities.FormrunnerFact;
import com.biit.factmanager.persistence.repositories.FormrunnerFactRepository;
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
    private static final long FACT_PROFESSIONAL_ID = 1;
    private static final String FACT_EXAMINATION_NAME = "test";

    @Autowired
    private FormrunnerFactRepository formrunnerFactRepository;

    private FormrunnerFact fact = null;

    @Test
    private void getAllAtTheBeginning() {
        Assert.assertEquals(formrunnerFactRepository.count(), 0);
    }

    @Test(dependsOnMethods = "getAllAtTheBeginning")
    private void addFact() {
        FormrunnerFact factToSave = new FormrunnerFact();
        factToSave.setPatientId(FACT_PATIENT_ID);
        factToSave.setExaminationName(FACT_EXAMINATION_NAME);
        factToSave.setCompanyId(FACT_COMPANY_ID);
        factToSave.setOrganizationId(FACT_ORGANIZATION_ID);
        factToSave.setProfessionalId(FACT_PROFESSIONAL_ID);
        fact = formrunnerFactRepository.save(factToSave);
        Assert.assertEquals(formrunnerFactRepository.count(), 1);
    }

    @Test(dependsOnMethods = "addFact")
    private void getFilteredFacts() {
        Assert.assertEquals(formrunnerFactRepository.count(), 1);
        // Filter by patientId and ExaminationName
        Collection<FormrunnerFact> retrievedFacts = formrunnerFactRepository.findByPatientIdAndExaminationName(FACT_PATIENT_ID,
                FACT_EXAMINATION_NAME);
        // Filter by companyId and ExaminationName
        retrievedFacts = formrunnerFactRepository.findByCompanyIdAndExaminationName(FACT_COMPANY_ID,
                FACT_EXAMINATION_NAME);
        Assert.assertEquals(retrievedFacts.size(), 1);

        // Filter by organizationId and ExaminationName
        retrievedFacts = formrunnerFactRepository.findByOrganizationIdAndExaminationName(FACT_ORGANIZATION_ID,
                FACT_EXAMINATION_NAME);
        Assert.assertEquals(retrievedFacts.size(), 1);
    }

    @Test(dependsOnMethods = "getFilteredFacts")
    private void deleteFact() {
        formrunnerFactRepository.delete(fact);
        Assert.assertEquals(formrunnerFactRepository.count(), 0);
    }
}
