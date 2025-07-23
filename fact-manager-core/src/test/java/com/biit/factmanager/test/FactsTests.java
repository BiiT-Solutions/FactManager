package com.biit.factmanager.test;

import com.biit.factmanager.core.providers.FactProvider;
import com.biit.factmanager.persistence.entities.FormrunnerVariableFact;
import com.biit.factmanager.persistence.entities.values.FormrunnerVariableValue;
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
    private static final String QUESTION_XPATH_1 = "/form/Category/Question1";
    private static final String VALUE_1 = "3";

    private static final String QUESTION_XPATH_2 = "/form/Category/Question2";
    private static final String VALUE_2 = "5";

    @Autowired
    private FactProvider<FormrunnerVariableFact> formrunnerVariableFactProvider;


    @BeforeClass
    public void databaseSetUp() {

    }

    @Test
    public void addFact() {
        Assert.assertEquals(formrunnerVariableFactProvider.count(), 0);
        FormrunnerVariableFact fact = formrunnerVariableFactProvider.save(new FormrunnerVariableFact());
        Assert.assertNotNull(fact);
        Assert.assertEquals(formrunnerVariableFactProvider.count(), 1);
        formrunnerVariableFactProvider.delete(fact);
    }

    @Test
    public void checkScore() {
        FormrunnerVariableFact formrunnerVariableFact = new FormrunnerVariableFact();
        FormrunnerVariableValue formrunnerVariableValue = new FormrunnerVariableValue();
        formrunnerVariableValue.setXpath(QUESTION_XPATH_1);
        formrunnerVariableValue.setValue("1");
        formrunnerVariableFact.setEntity(formrunnerVariableValue);

        Assert.assertEquals(formrunnerVariableFact.getEntity().getValue(), "1");
    }

    @Test
    public void searchByValueParameter() {
        Assert.assertEquals(formrunnerVariableFactProvider.count(), 0);
        FormrunnerVariableFact formrunnerVariableFact = new FormrunnerVariableFact();
        FormrunnerVariableValue formrunnerVariableValue = new FormrunnerVariableValue();
        formrunnerVariableValue.setXpath(QUESTION_XPATH_1);
        formrunnerVariableValue.setValue(VALUE_1);
        formrunnerVariableFact.setEntity(formrunnerVariableValue);
        formrunnerVariableFactProvider.save(formrunnerVariableFact);

        formrunnerVariableFact = new FormrunnerVariableFact();
        formrunnerVariableValue = new FormrunnerVariableValue();
        formrunnerVariableValue.setXpath(QUESTION_XPATH_2);
        formrunnerVariableValue.setValue(VALUE_2);
        formrunnerVariableFact.setEntity(formrunnerVariableValue);
        formrunnerVariableFactProvider.save(formrunnerVariableFact);

        Assert.assertEquals(formrunnerVariableFactProvider.getByValueParameter(0, 10, "xpath", QUESTION_XPATH_1).size(), 1);
        Assert.assertEquals(formrunnerVariableFactProvider.getByValueParameter(0, 10, "xpath", QUESTION_XPATH_2).size(), 1);
        Assert.assertEquals(formrunnerVariableFactProvider.getByValueParameter(0, 10, "value", VALUE_1).size(), 1);
        Assert.assertEquals(formrunnerVariableFactProvider.getByValueParameter(0, 10, "value", VALUE_2).size(), 1);
        Assert.assertEquals(formrunnerVariableFactProvider.getByValueParameter(0, 10, "value", "1000").size(), 0);
    }


    @AfterClass
    public void cleanDatabase() {
        formrunnerVariableFactProvider.getAll().forEach(fact -> formrunnerVariableFactProvider.delete(fact));
    }
}
