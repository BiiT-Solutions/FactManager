package com.biit.factmanager.test;

import com.biit.factmanager.persistence.entities.FormrunnerFact;
import com.biit.factmanager.persistence.repositories.FormrunnerFactRepository;
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
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@SpringBootTest
@Test(groups = "factRepository")
@Rollback(false)
public class FactRepositoryTests extends AbstractTransactionalTestNGSpringContextTests {

    private static final long FACT_TENANT_ID = 1;
    private static final String FACT_ELEMENT_ID = "elementId";
    private static final String FACT_CATEGORY = "category";
    private static final LocalDateTime FACT_DATE_BEFORE = LocalDateTime.now().minusDays(20);
    private static final LocalDateTime FACT_DATE_AFTER = LocalDateTime.now().plusDays(20);
    private static final LocalDateTime FACT_DATE_NOW = LocalDateTime.now();

    @Autowired
    private FormrunnerFactRepository formrunnerFactRepository;

    private FormrunnerFact fact = null;

    private Collection<FormrunnerFact> formrunnerFacts = null;

    @BeforeClass
    private void populate() {
        FormrunnerFact createdAtBeforeAndTenantIdAndCategory = new FormrunnerFact();
        FormrunnerFact createdAtNowAndTenantIdAndElementId = new FormrunnerFact();
        FormrunnerFact createdAtAfterAndElementIddAndCategory = new FormrunnerFact();

        createdAtBeforeAndTenantIdAndCategory.setTenantId(FACT_TENANT_ID);
        createdAtBeforeAndTenantIdAndCategory.setCategory(FACT_CATEGORY);
        createdAtBeforeAndTenantIdAndCategory.setCreatedAt(FACT_DATE_BEFORE);
        createdAtNowAndTenantIdAndElementId.setCreatedAt(FACT_DATE_AFTER);
        createdAtNowAndTenantIdAndElementId.setTenantId(FACT_TENANT_ID);
        createdAtNowAndTenantIdAndElementId.setElementId(FACT_ELEMENT_ID);
        createdAtAfterAndElementIddAndCategory.setCreatedAt(FACT_DATE_AFTER);
        createdAtAfterAndElementIddAndCategory.setElementId(FACT_ELEMENT_ID);
        createdAtAfterAndElementIddAndCategory.setCategory(FACT_CATEGORY);

        formrunnerFactRepository.save(createdAtBeforeAndTenantIdAndCategory);
        formrunnerFactRepository.save(createdAtNowAndTenantIdAndElementId);
        formrunnerFactRepository.save(createdAtAfterAndElementIddAndCategory);
    }

    @Test
    private void getAllAtTheBeginning() {
        Assert.assertEquals(StreamSupport.stream(formrunnerFactRepository.findAll().spliterator(),false)
                .count(), 3);
    }

    @Test(dependsOnMethods = "getAllAtTheBeginning")
    private void addFact() {
        FormrunnerFact factToSave = new FormrunnerFact();
        fact = formrunnerFactRepository.save(factToSave);
        Assert.assertEquals(formrunnerFactRepository.count(), 4);
        formrunnerFactRepository.delete(fact);
    }

    @Test(dependsOnMethods = "addFact")
    private void getFilteredFacts() {
        Assert.assertEquals(formrunnerFactRepository.count(), 3);
        Assert.assertEquals(formrunnerFactRepository.findByTenantIdAndCategoryAndCreatedAt
                (FACT_TENANT_ID,FACT_CATEGORY,FACT_DATE_BEFORE.minusDays(1),FACT_DATE_AFTER).stream().count(),1);
        Assert.assertEquals(formrunnerFactRepository.findByTenantIdAndElementIdAndCreatedAt
                (FACT_TENANT_ID,FACT_ELEMENT_ID,FACT_DATE_BEFORE,FACT_DATE_AFTER).stream().count(),1);
        Assert.assertEquals(formrunnerFactRepository.findByElementIdAndCategoryAndCreatedAt
                (FACT_ELEMENT_ID,FACT_CATEGORY,FACT_DATE_NOW,FACT_DATE_AFTER.plusDays(1)).stream().count(),1);
    }

    @Test(dependsOnMethods = "getFilteredFacts")
    private void factBetweenDates() {
        Assert.assertEquals(formrunnerFactRepository.findByCreatedAtGreaterThanEqualAndCreatedAtLessThanEqual
                (FACT_DATE_BEFORE,FACT_DATE_AFTER).stream().count(),3);
    }

    @Test(dependsOnMethods = "factBetweenDates")
    private void factBeforeDate() {
        Assert.assertEquals(formrunnerFactRepository.findByCreatedAtLessThan
                (FACT_DATE_NOW).stream().count(), 1);
    }

    @Test(dependsOnMethods = "factBeforeDate")
    private void factAfterDate() {
        Assert.assertEquals(formrunnerFactRepository.findByCreatedAtGreaterThan
                (FACT_DATE_NOW).stream().count(), 2);
    }

    @AfterClass
    private void deleteFact() {
        formrunnerFacts = StreamSupport.stream(formrunnerFactRepository
                .findAll().spliterator(), false).collect(Collectors.toList());
        for (FormrunnerFact formrunnerFact:formrunnerFacts) {
            formrunnerFactRepository.delete(formrunnerFact);
        }
        Assert.assertEquals(formrunnerFactRepository.count(), 0);
    }
}
