package com.biit.factmanager.test;

import com.biit.factmanager.persistence.entities.StringFact;
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
public class StringFactRepositoryTests extends AbstractTransactionalTestNGSpringContextTests {
    private static final String STRING_FACT = "stringFact";
    private static final String NEW_STRING_FACT = "newStringFact";
    private static final String STRING_FACT_UPDATED = "stringFactUpdated";
    private static final String FACT_ELEMENT_ID = "factElementId";
    private static final LocalDateTime FACT_TIME_FUTURE = LocalDateTime.now().plusDays(1);
    private static final LocalDateTime FACT_TIME_PAST = LocalDateTime.now().minusDays(1);
    private long stringFactRepositorySize = 0;

    @Autowired
    private FactRepository<StringFact> stringFactRepository;

    @Test
    private void createStringFact() {
        stringFactRepositorySize = stringFactRepository.count();
        StringFact stringFact = new StringFact();
        stringFact.setString(STRING_FACT);
        stringFactRepository.save(stringFact);
        Assert.assertEquals(stringFactRepository.count(), stringFactRepositorySize += 1);
    }

    @Test(dependsOnMethods = "createStringFact")
    private void readFact() {
        StringFact stringFact = new StringFact();
        stringFact.setElementId(FACT_ELEMENT_ID);
        stringFactRepository.save(stringFact);
        Assert.assertTrue(stringFactRepository.findByElementIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThanEqual
                (FACT_ELEMENT_ID, FACT_TIME_PAST, FACT_TIME_FUTURE).contains(stringFact));
        stringFactRepository.delete(stringFact);
    }

    @Test(dependsOnMethods = "createStringFact")
    private void searchFactByValue() {
        Collection<StringFact> facts = stringFactRepository.findByValueParameters(Pair.of("string", STRING_FACT));
        Assert.assertEquals(facts.size(), 1);
    }

    @Test(dependsOnMethods = "createStringFact")
    private void searchFactByInvalidValue() {
        Assert.assertEquals(stringFactRepository.findByValueParameters(Pair.of("string", STRING_FACT + "!")).size(), 0);
    }

    @Test(dependsOnMethods = {"searchFactByValue", "searchFactByInvalidValue", "readFact"})
    private void updateStringFact() throws Exception {
        StringFact stringFact = new StringFact();
        stringFact.setString(NEW_STRING_FACT);
        StringFact savedStringFact = stringFactRepository.save(stringFact);
        Assert.assertEquals((stringFactRepository.findById(savedStringFact.getId()).orElseThrow(Exception::new)).getString(), NEW_STRING_FACT);
        StringFact updatedStringFact = stringFactRepository.findById(savedStringFact.getId()).orElseThrow(Exception::new);
        updatedStringFact.setString(STRING_FACT_UPDATED);
        stringFactRepository.save(updatedStringFact);
        Assert.assertEquals(stringFactRepository.count(), stringFactRepositorySize += 1);
        Assert.assertEquals((stringFactRepository.findById(savedStringFact.getId()).orElseThrow(Exception::new)).getString(), STRING_FACT_UPDATED);
    }

    @Test(dependsOnMethods = "updateStringFact")
    private void searchFactByUpdatedValue() {
        Assert.assertEquals(stringFactRepository.findByValueParameters(Pair.of("string", STRING_FACT_UPDATED)).size(), 1);
    }

    @Test(dependsOnMethods = "updateStringFact")
    private void searchFactByOldValue() {
        Assert.assertEquals(stringFactRepository.findByValueParameters(Pair.of("string", NEW_STRING_FACT)).size(), 0);
    }

    @Test(dependsOnMethods = {"updateStringFact", "searchFactByUpdatedValue", "searchFactByOldValue"})
    private void deleteStringFact() {
        Assert.assertEquals(stringFactRepository.count(), stringFactRepositorySize);
        List<StringFact> stringFacts = stringFactRepository.findAll();
        Assert.assertEquals(stringFacts.size(), stringFactRepositorySize);
        stringFactRepository.delete(stringFacts.get(0));
        Assert.assertEquals(stringFactRepository.count(), stringFactRepositorySize -= 1);
    }

    @AfterClass
    private void clean() {
        stringFactRepository.findAll().
                forEach(stringValueFact -> stringFactRepository.delete(stringValueFact));
    }
}
