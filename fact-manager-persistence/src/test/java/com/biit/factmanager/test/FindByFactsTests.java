package com.biit.factmanager.test;

import com.biit.factmanager.persistence.entities.StringFact;
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
    private FactRepository<StringFact> factRepository;

    @BeforeClass
    private void populate() {
        factRepository.deleteAll();
        int repositorySize = factRepository.findAll().size();
        for (int i = 1; i <= 3; i++) {
            StringFact stringFact = new StringFact();
            stringFact.setOrganizationId(String.valueOf(i));
            stringFact.setTenantId(String.valueOf(i));
            stringFact.setTag(String.valueOf(i));
            stringFact.setGroup(String.valueOf(i));
            stringFact.setElementId(String.valueOf(i));
            stringFact.setCreatedAt(LocalDateTime.now());

            factRepository.save(stringFact);
        }
        Assert.assertEquals((repositorySize + 3), factRepository.findAll().size());
    }

    @Test
    public void getFindBy() {
        Collection<StringFact> fact1 = factRepository.findBy("1", "1", "1", "1", "1",
                LocalDateTime.now().minusDays(30), LocalDateTime.now().plusDays(1));

        Collection<StringFact> fact2 = factRepository.findBy("2", "2", "2", "2", "2",
                LocalDateTime.now().minusDays(30), LocalDateTime.now().plusDays(1));

        Collection<StringFact> fact3 = factRepository.findBy("3", "3", "3", "3", "3",
                LocalDateTime.now().minusDays(30), LocalDateTime.now().plusDays(1));

        Assert.assertEquals(fact1.size(), 1);
        Assert.assertEquals(fact2.size(), 1);
        Assert.assertEquals(fact3.size(), 1);
    }

    @Test
    public void getFindbyOrganizationId() {
        Assert.assertEquals(factRepository.findByOrganizationId("1").size(), 1);
        Assert.assertEquals(factRepository.findByOrganizationId("2").size(), 1);
        Assert.assertEquals(factRepository.findByOrganizationId("3").size(), 1);
    }

    @AfterClass
    private void tearDown() {
        factRepository.deleteAll();
        Assert.assertEquals(factRepository.findAll().size(), 0);
    }
}
