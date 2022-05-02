package com.biit.factmanager.test;

import com.biit.factmanager.core.providers.FactProvider;
import com.biit.factmanager.persistence.entities.FormrunnerQuestionFact;
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
    private FactServices<FormrunnerQuestionFact> factServices;

    @Autowired
    private FactProvider<FormrunnerQuestionFact> factProvider;


    @BeforeClass
    public void databaseSetUp() {

    }

    @Test
    public void addFacts() {
        Assert.assertEquals(factServices.getFacts(null, null, null, null, FACT_EXAMINATION_GROUP, null, null, null, null, null).size(), 0);
        // Save 2 empty facts
        FormrunnerQuestionFact FormrunnerQuestionFact = new FormrunnerQuestionFact();
        FormrunnerQuestionFact.setGroup(FACT_EXAMINATION_GROUP);
        List<FormrunnerQuestionFact> facts = new ArrayList<>();
        facts.add(FormrunnerQuestionFact);
        FormrunnerQuestionFact = new FormrunnerQuestionFact();
        FormrunnerQuestionFact.setGroup(FACT_EXAMINATION_GROUP);
        facts.add(FormrunnerQuestionFact);
        Assert.assertEquals(facts.size(), 2);
        factServices.addFactList(facts, null);
        // 2 saved + the one added at the beginning
        Assert.assertEquals(factServices.getFacts(null, null, null, null, FACT_EXAMINATION_GROUP, null, null, null, null, null).size(), 2);
    }

    @Test(dependsOnMethods = "addFacts")
    public void removeFact() {
        Collection<FormrunnerQuestionFact> facts = factServices.getFacts(null, null, null, null, FACT_EXAMINATION_GROUP, null, null, null, null, null);
        Assert.assertEquals(facts.size(), 2);
        Assert.assertNotNull(facts);
        for (FormrunnerQuestionFact fact : facts) {
            factServices.deleteFact(fact, null);
        }
        Assert.assertEquals(factServices.getFacts(null, null, null, null, FACT_EXAMINATION_GROUP, null, null, null, null, null).size(), 0);
    }


    @AfterClass
    public void cleanDatabase() {
        for (FormrunnerQuestionFact fact : factProvider.getAll()) {
            factServices.deleteFact(fact, null);
        }
    }
}
