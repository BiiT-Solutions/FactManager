package com.biit.factmanager.test;

import com.biit.factmanager.core.providers.FactProvider;
import com.biit.factmanager.persistence.entities.FormrunnerQuestionFact;
import com.biit.factmanager.persistence.entities.values.FormrunnerQuestionValue;
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
    private FactProvider<FormrunnerQuestionFact> factProvider;

    private FormrunnerQuestionFact fact = null;


    @BeforeClass
    public void databaseSetUp() {

    }

    @Test
    public void addFact() {
        Assert.assertEquals(factProvider.count(), 0);
        fact = factProvider.save(new FormrunnerQuestionFact());
        Assert.assertNotNull(fact);
        Assert.assertEquals(factProvider.count(), 1);
        factProvider.delete(fact);
    }

    @Test
    public void checkScore() {
        FormrunnerQuestionFact formrunnerQuestionFact = new FormrunnerQuestionFact();
        FormrunnerQuestionValue formrunnerQuestionValue = new FormrunnerQuestionValue();
        formrunnerQuestionValue.setQuestion(QUESTION_1);
        formrunnerQuestionValue.setAnswer(ANSWER_1);
        formrunnerQuestionValue.getVariables().put(FormrunnerQuestionValue.SCORE_VALUE, 1d);
        formrunnerQuestionFact.setEntity(formrunnerQuestionValue);

        Assert.assertEquals(formrunnerQuestionFact.getEntity().getVariables().get(FormrunnerQuestionValue.SCORE_VALUE), 1d);
    }

    @Test
    public void searchByValueParameter() {
        Assert.assertEquals(factProvider.count(), 0);
        FormrunnerQuestionFact formrunnerQuestionFact = new FormrunnerQuestionFact();
        FormrunnerQuestionValue formrunnerQuestionValue = new FormrunnerQuestionValue();
        formrunnerQuestionValue.setQuestion(QUESTION_1);
        formrunnerQuestionValue.setAnswer(ANSWER_1);
        formrunnerQuestionFact.setEntity(formrunnerQuestionValue);
        factProvider.save(formrunnerQuestionFact);

        formrunnerQuestionFact = new FormrunnerQuestionFact();
        formrunnerQuestionValue = new FormrunnerQuestionValue();
        formrunnerQuestionValue.setQuestion(QUESTION_2);
        formrunnerQuestionValue.setAnswer(ANSWER_2);
        formrunnerQuestionFact.setEntity(formrunnerQuestionValue);
        factProvider.save(formrunnerQuestionFact);

        Assert.assertEquals(factProvider.getByValueParameter("question", QUESTION_1).size(), 1);
        Assert.assertEquals(factProvider.getByValueParameter("question", QUESTION_2).size(), 1);
        Assert.assertEquals(factProvider.getByValueParameter("answer", ANSWER_1).size(), 2);
    }


    @AfterClass
    public void cleanDatabase() {
        factProvider.getAll().forEach(fact -> factProvider.delete(fact));
    }
}
