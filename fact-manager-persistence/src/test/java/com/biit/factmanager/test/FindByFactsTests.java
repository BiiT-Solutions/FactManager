package com.biit.factmanager.test;

import com.biit.factmanager.persistence.entities.LogFact;
import com.biit.factmanager.persistence.repositories.FactRepository;
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
public class FindByFactsTests extends AbstractTransactionalTestNGSpringContextTests {

    @Autowired
    private FactRepository<LogFact> factRepository;

    @BeforeClass
    private void populate() {
        factRepository.deleteAll();
        int repositorySize = factRepository.findAll().size();
        for (int i = 1; i <= 3; i++) {
            LogFact logFact = new LogFact();
            logFact.setOrganization(String.valueOf(i));
            logFact.setCreatedBy(String.valueOf(i));
            logFact.setApplication(String.valueOf(i));
            logFact.setTenant(String.valueOf(i));
            logFact.setTag(String.valueOf(i));
            logFact.setGroup(String.valueOf(i));
            logFact.setElement(String.valueOf(i));
            logFact.setCreatedAt(LocalDateTime.now());

            factRepository.save(logFact);
        }
        Assert.assertEquals((repositorySize + 3), factRepository.findAll().size());
    }

    @Test
    public void getFindBy() {
        Collection<LogFact> fact1 = factRepository.findBy("1", "1", "1", "1", "1", "1", "1", null,
                LocalDateTime.now().minusDays(30), LocalDateTime.now().plusDays(1));

        Collection<LogFact> fact2 = factRepository.findBy("2", "2", "2", "2", "2", "2", "2", null,
                LocalDateTime.now().minusDays(30), LocalDateTime.now().plusDays(1));

        Collection<LogFact> fact3 = factRepository.findBy("3", "3", "3", "3", "3", "3", "3", null,
                LocalDateTime.now().minusDays(30), LocalDateTime.now().plusDays(1));

        Assert.assertEquals(fact1.size(), 1);
        Assert.assertEquals(fact2.size(), 1);
        Assert.assertEquals(fact3.size(), 1);
    }

    @Test
    public void getFindByOrganizationId() {
        Assert.assertEquals(factRepository.findByOrganization("1").size(), 1);
        Assert.assertEquals(factRepository.findByOrganization("2").size(), 1);
        Assert.assertEquals(factRepository.findByOrganization("3").size(), 1);
    }

    @AfterClass(alwaysRun = true)
    private void tearDown() {
        factRepository.deleteAll();
        Assert.assertEquals(factRepository.findAll().size(), 0);
    }
}
