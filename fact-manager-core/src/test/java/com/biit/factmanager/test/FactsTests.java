package com.biit.factmanager.test;

/*-
 * #%L
 * FactManager (core)
 * %%
 * Copyright (C) 2020 - 2025 BiiT Sourcing Solutions S.L.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

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
