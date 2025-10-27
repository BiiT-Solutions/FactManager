package com.biit.factmanager.test;

/*-
 * #%L
 * FactManager (Rest)
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
import com.biit.factmanager.dto.FactDTO;
import com.biit.factmanager.persistence.entities.FormrunnerQuestionFact;
import com.biit.factmanager.persistence.entities.values.FormrunnerQuestionValue;
import com.biit.factmanager.rest.api.FactServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@Test(groups = {"factsServices"})
public class FactsServicesTests extends AbstractTransactionalTestNGSpringContextTests {

    private static final String FACT_EXAMINATION_GROUP = "examination_name";

    @Autowired
    private FactServices<FormrunnerQuestionValue> factServices;

    @Autowired
    private FactProvider<FormrunnerQuestionFact> factProvider;


    @BeforeClass
    public void databaseSetUp() {

    }

    @Test
    public void addFacts() {
        long previousFacts = factProvider.count();
        Assert.assertEquals(factServices.getFacts(null, null, null, null, null, null, FACT_EXAMINATION_GROUP, null, null,
                null, null, null, null, null, null, null, Optional.empty(), Optional.empty(), null, null, null).size(), 0);
        // Save 2 empty facts
        FormrunnerQuestionFact FormrunnerQuestionFact = new FormrunnerQuestionFact();
        FormrunnerQuestionFact.setGroup(FACT_EXAMINATION_GROUP);
        List<FormrunnerQuestionFact> facts = new ArrayList<>();
        facts.add(FormrunnerQuestionFact);
        FormrunnerQuestionFact = new FormrunnerQuestionFact();
        FormrunnerQuestionFact.setGroup(FACT_EXAMINATION_GROUP);
        facts.add(FormrunnerQuestionFact);
        Assert.assertEquals(facts.size(), 2);
        factProvider.saveAll(facts);
        Assert.assertEquals(factProvider.count(), previousFacts + 2);
        // 2 saved
        Assert.assertEquals(factServices.getFacts(null, null, null, null, null, null, FACT_EXAMINATION_GROUP, null, null, null,
                null, null, null, null, null, null, Optional.empty(), Optional.empty(), null, null, null).size(), 2);
    }

    @Test(dependsOnMethods = "addFacts")
    public void removeFact() {
        Collection<FactDTO> facts = factServices.getFacts(null, null, null, null, null, null, FACT_EXAMINATION_GROUP, null, null, null,
                null, null, null, null, null, null, Optional.empty(), Optional.empty(), null, null, null);
        Assert.assertEquals(facts.size(), 2);
        Assert.assertNotNull(facts);
        for (FactDTO fact : facts) {
            factServices.deleteFact(fact, null);
        }
        Assert.assertEquals(factServices.getFacts(null, null, null, null, null, null, FACT_EXAMINATION_GROUP, null, null, null, null,
                null, null, null, null, null, Optional.empty(), Optional.empty(), null, null, null).size(), 0);
    }


    @AfterClass
    public void cleanDatabase() {
        factProvider.deleteAll();
    }
}
