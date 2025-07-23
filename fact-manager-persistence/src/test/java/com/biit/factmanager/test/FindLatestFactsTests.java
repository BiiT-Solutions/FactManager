package com.biit.factmanager.test;

import com.biit.factmanager.persistence.entities.Fact;
import com.biit.factmanager.persistence.entities.FormrunnerFact;
import com.biit.factmanager.persistence.entities.values.FormrunnerValue;
import com.biit.factmanager.persistence.repositories.FactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;

@SpringBootTest
@Test(groups = "factRepository")
@Rollback(false)
public class FindLatestFactsTests extends AbstractTransactionalTestNGSpringContextTests {
    private static final String APPLICATION = "ApplicationTest";
    private static final String ORGANIZATION = "OrganizationTest";
    private static final String FORM = "The Form";
    private static final String VERSION = "1";

    private static final String USER_1 = "User1";
    private static final String USER_2 = "User2";
    private static final String USER_3 = "User3";


    @Autowired
    private FactRepository<FormrunnerFact> factRepository;

    private LocalDateTime formTime;

    private FormrunnerFact createFact(String createdBy) {
        final FormrunnerFact formrunnerFact = new FormrunnerFact();
        formrunnerFact.setOrganization(ORGANIZATION);
        formrunnerFact.setCreatedBy(createdBy);
        formrunnerFact.setApplication(APPLICATION);
        formrunnerFact.setEntity(new FormrunnerValue(FORM, VERSION, null, createdBy));
        return formrunnerFact;
    }

    @BeforeClass
    private void populate() throws InterruptedException {
        factRepository.deleteAll();
        int repositorySize = factRepository.findAll().size();


        formTime = LocalDateTime.now();
        factRepository.save(createFact(USER_1));
        Thread.sleep(1000);
        factRepository.save(createFact(USER_2));
        Thread.sleep(1000);
        factRepository.save(createFact(USER_1));
        Thread.sleep(1000);
        factRepository.save(createFact(USER_3));
        Thread.sleep(1000);
        factRepository.save(createFact(USER_2));
        Thread.sleep(1000);
        factRepository.save(createFact(USER_1));

        Assert.assertEquals((repositorySize + 6), factRepository.findAll().size());
    }

    @Test
    public void getLatestBy() {
        Collection<FormrunnerFact> facts = factRepository.findBy(FormrunnerFact.class, ORGANIZATION, null, null, APPLICATION, null, null, null, null, null, null, null, null, null,
                true, null,  null, PageRequest.of(0, 10));

        //3 users
        Assert.assertEquals(facts.size(), 3);
        Assert.assertTrue(facts.stream().map(Fact::getCreatedBy).toList().contains(USER_1));
        Assert.assertTrue(facts.stream().map(Fact::getCreatedBy).toList().contains(USER_2));
        Assert.assertTrue(facts.stream().map(Fact::getCreatedBy).toList().contains(USER_3));

        //Ensure lastest
        Assert.assertEquals(facts.stream().filter(formrunnerFact -> Objects.equals(formrunnerFact.getCreatedBy(), USER_1)).findFirst().get().getCreatedAt().getSecond(), formTime.plusSeconds(5).getSecond());
        Assert.assertEquals(facts.stream().filter(formrunnerFact -> Objects.equals(formrunnerFact.getCreatedBy(), USER_2)).findFirst().get().getCreatedAt().getSecond(), formTime.plusSeconds(4).getSecond());
        Assert.assertEquals(facts.stream().filter(formrunnerFact -> Objects.equals(formrunnerFact.getCreatedBy(), USER_3)).findFirst().get().getCreatedAt().getSecond(), formTime.plusSeconds(3).getSecond());

    }

    @AfterClass(alwaysRun = true)
    private void tearDown() {
        factRepository.deleteAll();
        Assert.assertEquals(factRepository.findAll().size(), 0);
    }
}
