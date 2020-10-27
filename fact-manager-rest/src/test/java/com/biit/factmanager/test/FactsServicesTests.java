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

@SpringBootTest
@Test(groups = { "factsServices" })
public class FactsServicesTests extends AbstractTestNGSpringContextTests {


    @Autowired
    private FactServices factServices;

    @Autowired
    private FactRepository factRepository;

    private Fact fact = null;


    @BeforeClass
    public void databaseSetUp() {

    }

    @Test
    public void addFact() {
        // One fact is added by default to test
        Assert.assertEquals(factServices.getAllFacts(null).size(), 1);
        fact = factServices.addFact(new Fact(), null);
        Assert.assertNotNull(fact);
        Assert.assertEquals(factServices.getAllFacts(null).size(), 2);
    }

    @Test(dependsOnMethods = "addFact")
    public void removeFact() {
        Assert.assertEquals(factServices.getAllFacts(null).size(), 2);
        factServices.deleteFact(fact, null);
        Assert.assertNotNull(fact);
        Assert.assertEquals(factServices.getAllFacts(null).size(), 1);
        fact = null;
    }


    @AfterClass
    public void cleanDatabase() {
        if(fact != null){
            factRepository.delete(fact);
        }
    }
}
