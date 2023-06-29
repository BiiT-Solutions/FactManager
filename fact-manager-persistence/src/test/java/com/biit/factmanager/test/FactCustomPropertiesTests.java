package com.biit.factmanager.test;

import com.biit.factmanager.persistence.entities.LogFact;
import com.biit.factmanager.persistence.repositories.FactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
@Test(groups = "factRepository")
@Rollback(false)
public class FactCustomPropertiesTests extends AbstractTransactionalTestNGSpringContextTests {

    @Autowired
    private FactRepository<LogFact> logFactRepository;

    private Long factId;

    @Test
    private void createFact() {
        LogFact logFact = new LogFact();
        logFact.setString("Fact1");

        Map<String, String> properties = new HashMap<>();
        properties.put("property1", "value1");
        properties.put("property2", "value2");
        logFact.setCustomProperties(properties);

        factId = logFactRepository.save(logFact).getId();
    }

    @Test(dependsOnMethods = "createFact")
    private void readFact() {
        LogFact logFact = logFactRepository.findById(factId).orElseThrow();
        Assert.assertEquals(logFact.getCustomProperties().get("property1"), "value1");
        Assert.assertEquals(logFact.getCustomProperties().get("property2"), "value2");
        Assert.assertNull(logFact.getCustomProperties().get("property3"));
    }

    @Test(dependsOnMethods = "createFact")
    private void findByCustomProperty() {
        Map<String, String> properties = new HashMap<>();
        properties.put("property1", "value1");
        List<LogFact> logFacts = logFactRepository.findByCustomProperties(properties);
        Assert.assertEquals(logFacts.size(), 1);

        properties.put("property2", "value2");
        logFacts = logFactRepository.findByCustomProperties(properties);
        Assert.assertEquals(logFacts.size(), 1);

        properties.put("property1", "value2");
        logFacts = logFactRepository.findByCustomProperties(properties);
        Assert.assertEquals(logFacts.size(), 0);
    }

    @AfterClass(alwaysRun = true)
    private void clean() {
        logFactRepository.deleteAll();
    }
}
