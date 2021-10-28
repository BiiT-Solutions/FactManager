package com.biit.factmanager.test;

import com.biit.factmanager.core.providers.FormrunnerFactProvider;
import com.biit.factmanager.persistence.entities.FormrunnerFact;
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
    private FormrunnerFactProvider formrunnerFactProvider;

    private FormrunnerFact fact = null;


    @BeforeClass
    public void databaseSetUp() {

    }

    @Test
    public void addFact() {
        // Added using the data.sql, so 1 fact is in the DB
        Assert.assertEquals(formrunnerFactProvider.count(), 1);
        fact = formrunnerFactProvider.add(new FormrunnerFact());
        Assert.assertNotNull(fact);
        Assert.assertEquals(formrunnerFactProvider.count(), 2);
    }


    @AfterClass
    public void cleanDatabase() {
        if (fact != null) {
            formrunnerFactProvider.delete(fact);
        }
    }
}
