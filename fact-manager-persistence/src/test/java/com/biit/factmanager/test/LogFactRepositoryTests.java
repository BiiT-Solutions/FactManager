package com.biit.factmanager.test;

import com.biit.factmanager.persistence.entities.LogFact;
import com.biit.factmanager.persistence.repositories.FactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Pair;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@SpringBootTest
@Test(groups = "factRepository")
@Rollback(false)
public class LogFactRepositoryTests extends AbstractTransactionalTestNGSpringContextTests {
    private static final String STRING_FACT = "stringFact";
    private static final String NEW_STRING_FACT = "newStringFact";
    private static final String STRING_FACT_UPDATED = "stringFactUpdated";
    private static final String FACT_ELEMENT_ID = "factElementId";
    private static final LocalDateTime FACT_TIME_FUTURE = LocalDateTime.now().plusDays(1);
    private static final LocalDateTime FACT_TIME_PAST = LocalDateTime.now().minusDays(1);
    private long stringFactRepositorySize = 0;

    @Autowired
    private FactRepository<LogFact> logFactRepository;

    @Test
    private void createFact() {
        stringFactRepositorySize = logFactRepository.count();
        LogFact logFact = new LogFact();
        logFact.setString(STRING_FACT);
        logFactRepository.save(logFact);
        Assert.assertEquals(logFactRepository.count(), stringFactRepositorySize += 1);
    }

    @Test(dependsOnMethods = "createFact")
    private void readFact() {
        LogFact logFact = new LogFact();
        logFact.setElement(FACT_ELEMENT_ID);
        logFactRepository.save(logFact);
        Assert.assertTrue(logFactRepository.findByElementAndCreatedAtGreaterThanEqualAndCreatedAtLessThanEqual
                (FACT_ELEMENT_ID, FACT_TIME_PAST, FACT_TIME_FUTURE).contains(logFact));
        logFactRepository.delete(logFact);
    }

    @Test(dependsOnMethods = "createFact")
    private void searchFactByValue() {
        Collection<LogFact> facts = logFactRepository.findByValueParameters(Pair.of("string", STRING_FACT));
        Assert.assertEquals(facts.size(), 1);
    }

    @Test(dependsOnMethods = "createFact")
    private void searchFactByInvalidValue() {
        Assert.assertEquals(logFactRepository.findByValueParameters(Pair.of("string", STRING_FACT + "!")).size(), 0);
    }

    @Test(dependsOnMethods = {"searchFactByValue", "searchFactByInvalidValue", "readFact"})
    private void updateStringFact() throws Exception {
        LogFact logFact = new LogFact();
        logFact.setString(NEW_STRING_FACT);
        LogFact savedLogFact = logFactRepository.save(logFact);
        Assert.assertEquals((logFactRepository.findById(savedLogFact.getId()).orElseThrow(Exception::new)).getString(), NEW_STRING_FACT);
        LogFact updatedLogFact = logFactRepository.findById(savedLogFact.getId()).orElseThrow(Exception::new);
        updatedLogFact.setString(STRING_FACT_UPDATED);
        logFactRepository.save(updatedLogFact);
        Assert.assertEquals(logFactRepository.count(), stringFactRepositorySize += 1);
        Assert.assertEquals((logFactRepository.findById(savedLogFact.getId()).orElseThrow(Exception::new)).getString(), STRING_FACT_UPDATED);
    }

    @Test(dependsOnMethods = "updateStringFact")
    private void searchFactByUpdatedValue() {
        Assert.assertEquals(logFactRepository.findByValueParameters(Pair.of("string", STRING_FACT_UPDATED)).size(), 1);
    }

    @Test(dependsOnMethods = "updateStringFact")
    private void searchFactByOldValue() {
        Assert.assertEquals(logFactRepository.findByValueParameters(Pair.of("string", NEW_STRING_FACT)).size(), 0);
    }

    @Test(dependsOnMethods = {"updateStringFact", "searchFactByUpdatedValue", "searchFactByOldValue"})
    private void deleteStringFact() {
        Assert.assertEquals(logFactRepository.count(), stringFactRepositorySize);
        List<LogFact> logFacts = logFactRepository.findAll();
        Assert.assertEquals(logFacts.size(), stringFactRepositorySize);
        logFactRepository.delete(logFacts.get(0));
        Assert.assertEquals(logFactRepository.count(), stringFactRepositorySize -= 1);
    }

    @AfterClass(alwaysRun = true)
    private void clean() {
        logFactRepository.deleteAll();
    }
}
