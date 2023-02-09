package com.biit.factmanager.test;

import com.biit.factmanager.persistence.entities.BasicFact;
import com.biit.factmanager.persistence.repositories.FactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@Test(groups = "factRepository")
@Rollback(false)
public class BasicFactRepositoryTests extends AbstractTransactionalTestNGSpringContextTests {
    private static final String BASIC_FACT = "basicFact";
    private static final String NEW_BASIC_FACT = "newBasicFact";
    private static final String STRING_FACT_UPDATED = "basicFactUpdated";
    private static final String FACT_ELEMENT_ID = "factElementId";
    private static final LocalDateTime FACT_TIME_FUTURE = LocalDateTime.now().plusDays(1);
    private static final LocalDateTime FACT_TIME_PAST = LocalDateTime.now().minusDays(1);
    private long stringFactRepositorySize = 0;

    @Autowired
    private FactRepository<BasicFact> basicFactFactRepository;

    @Test
    private void createStringFact() {
        stringFactRepositorySize = basicFactFactRepository.count();
        BasicFact basicFact = new BasicFact();
        basicFact.setValue(BASIC_FACT);
        basicFactFactRepository.save(basicFact);
        Assert.assertEquals(basicFactFactRepository.count(), stringFactRepositorySize += 1);
    }

    @Test(dependsOnMethods = "createStringFact")
    private void readFact() {
        BasicFact basicFact = new BasicFact();
        basicFact.setElement(FACT_ELEMENT_ID);
        basicFactFactRepository.save(basicFact);
        Assert.assertTrue(basicFactFactRepository.findByElementAndCreatedAtGreaterThanEqualAndCreatedAtLessThanEqual
                (FACT_ELEMENT_ID, FACT_TIME_PAST, FACT_TIME_FUTURE).contains(basicFact));
        basicFactFactRepository.delete(basicFact);
    }

    @Test(dependsOnMethods = {"readFact"})
    private void updateStringFact() throws Exception {
        BasicFact basicFact = new BasicFact();
        basicFact.setValue(NEW_BASIC_FACT);
        BasicFact savedStringFact = basicFactFactRepository.save(basicFact);
        Assert.assertEquals((basicFactFactRepository.findById(savedStringFact.getId()).orElseThrow(Exception::new)).getValue(), NEW_BASIC_FACT);
        BasicFact updatedStringFact = basicFactFactRepository.findById(savedStringFact.getId()).orElseThrow(Exception::new);
        updatedStringFact.setValue(STRING_FACT_UPDATED);
        basicFactFactRepository.save(updatedStringFact);
        Assert.assertEquals(basicFactFactRepository.count(), stringFactRepositorySize += 1);
        Assert.assertEquals((basicFactFactRepository.findById(savedStringFact.getId()).orElseThrow(Exception::new)).getValue(), STRING_FACT_UPDATED);
    }

    @Test(dependsOnMethods = {"updateStringFact"})
    private void deleteStringFact() {
        Assert.assertEquals(basicFactFactRepository.count(), stringFactRepositorySize);
        List<BasicFact> stringFacts = basicFactFactRepository.findAll();
        Assert.assertEquals(stringFacts.size(), stringFactRepositorySize);
        basicFactFactRepository.delete(stringFacts.get(0));
        Assert.assertEquals(basicFactFactRepository.count(), stringFactRepositorySize -= 1);
    }

    @AfterClass
    private void clean() {
        basicFactFactRepository.findAll().
                forEach(stringValueFact -> basicFactFactRepository.delete(stringValueFact));
    }
}
