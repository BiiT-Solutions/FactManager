package com.biit.factmanager.test;

import com.biit.factmanager.persistence.entities.FormrunnerFact;
import com.biit.factmanager.persistence.enums.Level;
import com.biit.factmanager.rest.api.FactServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@Test(groups = {"factsServices"})
public class FactsServicesTests extends AbstractTestNGSpringContextTests {

    private static final String FACT_EXAMINATION_NAME = "examination_name";
    private static final Long FACT_ID = 1L;

    @Autowired
    private FactServices factServices;

    private List<FormrunnerFact> facts = new ArrayList<>();


    @BeforeClass
    public void databaseSetUp() {

    }

    @Test
    public void addFacts() {
        // One fact is added by default to test
        Assert.assertEquals(factServices.getFacts(FACT_ID, FACT_EXAMINATION_NAME, Level.COMPANY, null).size(), 1);
        // Save 2 empty facts
        facts.add(new FormrunnerFact());
        facts.add(new FormrunnerFact());
        facts = factServices.addFactList(facts, null);
        Assert.assertNotNull(facts);
        Assert.assertEquals(facts.size(), 2);
        // 2 saved + the one added at the beginning
        Assert.assertEquals(factServices.getFacts(FACT_ID, FACT_EXAMINATION_NAME, null, null).size(), 3);
    }

    @Test(dependsOnMethods = "addFacts")
    public void removeFact() {
        Assert.assertEquals(factServices.getFacts(FACT_ID, "", null, null).size(), 3);
        Assert.assertNotNull(facts);
        for (FormrunnerFact fact : facts) {
            factServices.deleteFact(fact, null);
        }
        Assert.assertEquals(factServices.getFacts(FACT_ID, null, null, null).size(), 1);
    }


    @AfterClass
    public void cleanDatabase() {
        if (facts != null) {
            for (FormrunnerFact fact : facts) {
                factServices.deleteFact(fact, null);
                System.out.println("deleted fact ");
            }
        }
    }
}
