package com.biit.factmanager.test;

import com.biit.factmanager.core.providers.FactProvider;
import com.biit.factmanager.persistence.entities.FormRunnerFact;
import com.biit.factmanager.persistence.entities.values.FormRunnerValue;
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
    private static final String QUESTION_1 = "Question1";
    private static final String ANSWER_1 = "Answer1";

    private static final String QUESTION_2 = "Question2";
    private static final String ANSWER_2 = "Answer1";

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
        factProvider.delete(fact);
    }

    @Test
    public void searchByValueParameter() {
        Assert.assertEquals(factProvider.count(), 0);
        FormRunnerFact formRunnerFact = new FormRunnerFact();
        FormRunnerValue formRunnerValue = new FormRunnerValue();
        formRunnerValue.setQuestion(QUESTION_1);
        formRunnerValue.setAnswer(ANSWER_1);
        formRunnerFact.setEntity(formRunnerValue);
        factProvider.save(formRunnerFact);

        formRunnerFact = new FormRunnerFact();
        formRunnerValue = new FormRunnerValue();
        formRunnerValue.setQuestion(QUESTION_2);
        formRunnerValue.setAnswer(ANSWER_2);
        formRunnerFact.setEntity(formRunnerValue);
        factProvider.save(formRunnerFact);

        Assert.assertEquals(factProvider.getByValueParameter("question", QUESTION_1).size(), 1);
        Assert.assertEquals(factProvider.getByValueParameter("question", QUESTION_2).size(), 1);
        Assert.assertEquals(factProvider.getByValueParameter("answer", ANSWER_1).size(), 2);
    }


    @AfterClass
    public void cleanDatabase() {
        factProvider.getAll().forEach(fact -> factProvider.delete(fact));
    }
}
