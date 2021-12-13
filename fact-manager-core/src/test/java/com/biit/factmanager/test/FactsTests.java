package com.biit.factmanager.test;

import com.biit.factmanager.core.providers.FactProvider;
import com.biit.factmanager.persistence.entities.FormRunnerFact;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@SpringBootTest
@Test(groups = {"facts"})
public class FactsTests extends AbstractTestNGSpringContextTests {

    @Autowired
    private FactProvider<FormRunnerFact> factProvider;

    private FormRunnerFact fact = null;


    @BeforeClass
    public void databaseSetUp() {

    }

    @Test
    public void addFact() {
        Assert.assertEquals(factProvider.count(), 0);
        fact = factProvider.save(new FormRunnerFact());
        Assert.assertNotNull(fact);
        Assert.assertEquals(factProvider.count(), 1);
    }


    @AfterClass
    public void cleanDatabase() {
        if (fact != null) {
            factProvider.delete(fact);
        }
    }
}
