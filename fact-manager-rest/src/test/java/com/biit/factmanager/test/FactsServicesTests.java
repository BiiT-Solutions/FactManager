package com.biit.factmanager.test;

import com.biit.factmanager.persistence.entities.Fact;
import com.biit.factmanager.persistence.repositories.FactRepository;
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
@Test(groups = { "factsServices" })
public class FactsServicesTests extends AbstractTestNGSpringContextTests {


    @Autowired
    private FactServices factServices;
    

    private List<Fact> facts = new ArrayList<>();


    @BeforeClass
    public void databaseSetUp() {

    }

    @Test
    public void addFacts() {
        // One fact is added by default to test
        Assert.assertEquals(factServices.getAllFacts(null).size(), 1);
        // Save 2 empty facts
        facts.add(new Fact());
        facts.add(new Fact());
        facts = factServices.addFactList( facts, null);
        Assert.assertNotNull(facts);
        Assert.assertEquals(facts.size(), 2);
        // 2 saved + the one added at the beginning
        Assert.assertEquals(factServices.getAllFacts(null).size(), 3);
    }

    @Test(dependsOnMethods = "addFacts")
    public void removeFact() {
        Assert.assertEquals(factServices.getAllFacts(null).size(), 3);
        Assert.assertNotNull(facts);
        for (Fact fact: facts) {
            factServices.deleteFact(fact, null);
        }
        Assert.assertEquals(factServices.getAllFacts(null).size(), 1);
    }


    @AfterClass
    public void cleanDatabase() {
        if(facts != null){
            for (Fact fact: facts) {
                factServices.deleteFact(fact, null);
            }
        }
    }
}
