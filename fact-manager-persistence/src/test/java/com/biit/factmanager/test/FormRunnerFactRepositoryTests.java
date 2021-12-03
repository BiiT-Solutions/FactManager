package com.biit.factmanager.test;

import com.biit.factmanager.persistence.entities.FormRunnerFact;
import com.biit.factmanager.persistence.entities.values.FormRunnerValue;
import com.biit.factmanager.persistence.repositories.FormRunnerFactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.Collection;

@SpringBootTest
@Test(groups = "factRepository")
@Rollback(false)
public class FormRunnerFactRepositoryTests extends AbstractTransactionalTestNGSpringContextTests {

    private static final String FACT_TENANT_ID = "1";
    private static final long FACT_EXAMINATION_ID = 2;
    private static final long FACT_COMPANY_ID = 3;
    private static final long FACT_PATIENT_ID = 4;
    private static final long FACT_PROFESSIONAL_ID = 5;
    private static final String FACT_EXAMINATION_NAME = "test";
    private static final String FACT_ELEMENT_ID = "elementId";
    private static final String FACT_CATEGORY = "category";
    private static final LocalDateTime FACT_DATE_BEFORE = LocalDateTime.now().minusDays(20);
    private static final LocalDateTime FACT_DATE_AFTER = LocalDateTime.now().plusDays(20);
    private static final LocalDateTime FACT_DATE_NOW = LocalDateTime.now();

    @Autowired
    private FormRunnerFactRepository formRunnerFactRepository;


    @BeforeClass
    private void populate() {
        FormRunnerFact createdAtBeforeAndTenantIdAndCategory = new FormRunnerFact();
        FormRunnerFact createdAtNowAndTenantIdAndElementId = new FormRunnerFact();
        FormRunnerFact createdAtAfterAndElementIdAndCategory = new FormRunnerFact();

        createdAtBeforeAndTenantIdAndCategory.setTenantId(FACT_TENANT_ID);
        createdAtBeforeAndTenantIdAndCategory.setGroup(FACT_CATEGORY);
        createdAtBeforeAndTenantIdAndCategory.setCreatedAt(FACT_DATE_BEFORE);
        createdAtNowAndTenantIdAndElementId.setCreatedAt(FACT_DATE_AFTER);
        createdAtNowAndTenantIdAndElementId.setTenantId(FACT_TENANT_ID);
        createdAtNowAndTenantIdAndElementId.setElementId(FACT_ELEMENT_ID);
        createdAtAfterAndElementIdAndCategory.setCreatedAt(FACT_DATE_AFTER);
        createdAtAfterAndElementIdAndCategory.setElementId(FACT_ELEMENT_ID);
        createdAtAfterAndElementIdAndCategory.setGroup(FACT_CATEGORY);

        formRunnerFactRepository.save(createdAtBeforeAndTenantIdAndCategory);
        formRunnerFactRepository.save(createdAtNowAndTenantIdAndElementId);
        formRunnerFactRepository.save(createdAtAfterAndElementIdAndCategory);
    }

    @Test
    private void getAllAtTheBeginning() {
        Assert.assertEquals(formRunnerFactRepository.count(), 3);
    }

    @Test(dependsOnMethods = "getAllAtTheBeginning")
    private void addFact() {
        FormRunnerFact factToSave = new FormRunnerFact();
        factToSave.setGroup(FACT_EXAMINATION_NAME);
        factToSave.setElementId(FACT_EXAMINATION_ID + "");
        factToSave.setTenantId(FACT_PATIENT_ID + "");

        FormRunnerValue formRunnerValue = new FormRunnerValue();
        formRunnerValue.setCompanyId(FACT_COMPANY_ID);
        formRunnerValue.setProfessionalId(FACT_PROFESSIONAL_ID);
        factToSave.setEntity(formRunnerValue);

        FormRunnerFact fact = formRunnerFactRepository.save(factToSave);
        Assert.assertEquals(formRunnerFactRepository.count(), 4);


    }

    @Test(dependsOnMethods = "addFact")
    private void searchFactByValueParameters() {
        Collection<FormRunnerFact> facts = formRunnerFactRepository.findByValue(FACT_COMPANY_ID + "");

        facts.forEach(formRunnerFact -> formRunnerFactRepository.delete(formRunnerFact));
    }


    @Test(dependsOnMethods = "addFact")
    private void getFilteredFacts() {
        Assert.assertEquals(formRunnerFactRepository.count(), 3);
        Assert.assertEquals((long) formRunnerFactRepository.findByTenantIdAndGroupAndCreatedAt
                (FACT_TENANT_ID, FACT_CATEGORY, FACT_DATE_BEFORE.minusDays(1), FACT_DATE_AFTER).size(), 1);
        Assert.assertEquals((long) formRunnerFactRepository.findByTenantIdAndElementIdAndCreatedAt
                (FACT_TENANT_ID, FACT_ELEMENT_ID, FACT_DATE_BEFORE, FACT_DATE_AFTER).size(), 1);
        Assert.assertEquals((long) formRunnerFactRepository.findByElementIdAndGroupAndCreatedAt
                (FACT_ELEMENT_ID, FACT_CATEGORY, FACT_DATE_NOW, FACT_DATE_AFTER.plusDays(1)).size(), 1);
    }

    @Test(dependsOnMethods = "getFilteredFacts")
    private void factBetweenDates() {
        Assert.assertEquals(formRunnerFactRepository.findByCreatedAtGreaterThanEqualAndCreatedAtLessThanEqual
                (LocalDateTime.now().minusDays(21), LocalDateTime.now().plusDays(21)).size(), 3);
    }

    @Test(dependsOnMethods = "factBetweenDates")
    private void factBeforeDate() {
        Assert.assertEquals(formRunnerFactRepository.findByCreatedAtLessThan
                (LocalDateTime.now().plusDays(21)).size(), 3);
    }

    @Test(dependsOnMethods = "factBeforeDate")
    private void factAfterDate() {
        Assert.assertEquals(formRunnerFactRepository.findByCreatedAtGreaterThan
                (FACT_DATE_NOW).size(), 2);
    }

    @AfterClass
    private void deleteFact() {
        formRunnerFactRepository.findAll().
                forEach(formRunnerFact -> formRunnerFactRepository.delete(formRunnerFact));
        Assert.assertEquals(formRunnerFactRepository.count(), 0);
    }
}
