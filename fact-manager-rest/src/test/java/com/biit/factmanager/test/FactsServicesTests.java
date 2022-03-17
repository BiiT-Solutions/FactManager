package com.biit.factmanager.test;

import com.biit.factmanager.core.providers.FactProvider;
import com.biit.factmanager.persistence.entities.Fact;
import com.biit.factmanager.persistence.entities.FormRunnerFact;
import com.biit.factmanager.rest.api.FactServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SpringBootTest
@Test(groups = {"factsServices"})
public class FactsServicesTests extends AbstractTestNGSpringContextTests {

    private static final String FACT_EXAMINATION_GROUP = "examination_name";

    @Autowired
    private FactServices factServices;

    @Autowired
    private FactProvider factProvider;


    @BeforeClass
    public void databaseSetUp() {

    }

    @Test
    public void addFacts() {
        Assert.assertEquals(factServices.getFacts(null, null, null, null, FACT_EXAMINATION_GROUP, null, null, null, null, null).size(), 0);
        // Save 2 empty facts
        FormRunnerFact formRunnerFact = new FormRunnerFact();
        formRunnerFact.setGroup(FACT_EXAMINATION_GROUP);
        List<FormRunnerFact> facts = new ArrayList<>();
        facts.add(formRunnerFact);
        formRunnerFact = new FormRunnerFact();
        formRunnerFact.setGroup(FACT_EXAMINATION_GROUP);
        facts.add(formRunnerFact);
        Assert.assertEquals(facts.size(), 2);
        facts = factServices.addFactList(facts, null);
        // 2 saved + the one added at the beginning
        Assert.assertEquals(factServices.getFacts(null, null, null, null, FACT_EXAMINATION_GROUP, null, null, null, null, null).size(), 2);
    }

    @Test(dependsOnMethods = "addFacts")
    public void removeFact() {
        Collection<FormRunnerFact> facts = factServices.getFacts(null, null, null, null, FACT_EXAMINATION_GROUP, null, null, null, null, null);
        Assert.assertEquals(facts.size(), 2);
        Assert.assertNotNull(facts);
        for (FormRunnerFact fact : facts) {
            factServices.deleteFact(fact, null);
        }
        Assert.assertEquals(factServices.getFacts(null, null, null, null, FACT_EXAMINATION_GROUP, null, null, null, null, null).size(), 0);
    }


    @AfterClass
    public void cleanDatabase() {
        for (Object fact : factProvider.getAll()) {
            factServices.deleteFact((Fact) fact, null);
        }
    }
}
