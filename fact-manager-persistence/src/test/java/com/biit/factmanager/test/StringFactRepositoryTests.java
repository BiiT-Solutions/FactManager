package com.biit.factmanager.test;

import com.biit.factmanager.persistence.entities.Fact;
import com.biit.factmanager.persistence.entities.StringFact;
import com.biit.factmanager.persistence.entities.values.StringValue;
import com.biit.factmanager.persistence.repositories.StringFactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
    private static final String STRING_FACT_UPDATED = "stringFactUpdated";
    private static final String FACT_ELEMENT_ID = "factElementId";
    private static final LocalDateTime FACT_TIME_FUTURE = LocalDateTime.now().plusDays(1);
    private static final LocalDateTime FACT_TIME_PAST = LocalDateTime.now().minusDays(1);

    @Autowired
    private StringFactRepository stringFactRepository;

    @Test
    private void createStringFact() {
        Assert.assertEquals(stringFactRepository.count(), 0);
        StringFact stringFact = new StringFact();
        stringFact.setValue(STRING_FACT);
        stringFactRepository.save(stringFact);
        Assert.assertEquals(stringFactRepository.count(), 1);
    }

    @Test(dependsOnMethods = "createStringFact")
    private void readFact() {
        StringFact stringFact = new StringFact();
        stringFact.setElementId(FACT_ELEMENT_ID);
        stringFactRepository.save(stringFact);
        Assert.assertTrue(stringFactRepository.findByElementIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThanEqual
                (FACT_ELEMENT_ID, FACT_TIME_PAST, FACT_TIME_FUTURE).contains(stringFact));
    }

    @Test(dependsOnMethods = "createStringFact")
    private void updateStringFact() throws Exception {
        StringFact stringFact = new StringFact();
        stringFact.setString(STRING_FACT);
        StringFact savedStringFact = stringFactRepository.save(stringFact);
        Assert.assertEquals((stringFactRepository.findById(savedStringFact.getId()).orElseThrow(Exception::new)).getString(), STRING_FACT);
        stringFactRepository.findById(savedStringFact.getId()).orElseThrow(Exception::new).setValue(STRING_FACT_UPDATED);
        Assert.assertEquals((stringFactRepository.findById(savedStringFact.getId()).orElseThrow(Exception::new)).getValue(), STRING_FACT_UPDATED);
    }

    @Test(dependsOnMethods = {"createStringFact", "updateStringFact", "readFact"})
    private void deleteStringFact() {
        Assert.assertEquals(stringFactRepository.count(), 3);
        List<StringFact> stringFacts = stringFactRepository.findAll();
        Assert.assertEquals(stringFacts.size(), 3);
        stringFactRepository.delete(stringFacts.get(0));
        Assert.assertEquals(stringFactRepository.count(), 2);
    }

    @AfterClass
    private void clean() {
        stringFactRepository.findAll().
                forEach(stringValueFact -> stringFactRepository.delete(stringValueFact));
    }
}
