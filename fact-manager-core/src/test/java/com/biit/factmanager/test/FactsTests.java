package com.biit.factmanager.test;

import com.biit.factmanager.core.providers.FormRunnerFactProvider;
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
    private FormRunnerFactProvider formrunnerFactProvider;

    private FormRunnerFact fact = null;


    @BeforeClass
    public void databaseSetUp() {

    }

    @Test
    public void addFact() {
        Assert.assertEquals(formrunnerFactProvider.count(), 0);
        fact = formrunnerFactProvider.add(new FormRunnerFact());
        Assert.assertNotNull(fact);
        Assert.assertEquals(formrunnerFactProvider.count(), 1);
    }


    @AfterClass
    public void cleanDatabase() {
        if (fact != null) {
            formrunnerFactProvider.delete(fact);
        }
    }
}
