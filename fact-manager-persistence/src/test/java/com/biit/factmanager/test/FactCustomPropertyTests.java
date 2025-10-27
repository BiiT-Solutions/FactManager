package com.biit.factmanager.test;

/*-
 * #%L
 * FactManager (Persistence)
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

import com.biit.factmanager.persistence.entities.CustomProperty;
import com.biit.factmanager.persistence.entities.LogFact;
import com.biit.factmanager.persistence.repositories.FactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
@Test(groups = "factRepository")
@Rollback(false)
public class FactCustomPropertyTests extends AbstractTransactionalTestNGSpringContextTests {

    @Autowired
    private FactRepository<LogFact> logFactRepository;

    private Long factId;

    @Test
    private void createFact() {
        LogFact logFact = new LogFact();
        logFact.setString("Fact1");

        List<CustomProperty> customProperties = new ArrayList<>();
        customProperties.add(new CustomProperty(logFact, "property1", "value1"));
        customProperties.add(new CustomProperty(logFact, "property2", "value2"));
        logFact.setCustomProperties(customProperties);

        factId = logFactRepository.save(logFact).getId();
    }

    @Test(dependsOnMethods = "createFact")
    private void readFact() {
        LogFact logFact = logFactRepository.findById(factId).orElseThrow();
        Assert.assertEquals(logFact.getCustomProperties().size(), 2);
    }

    @Test(dependsOnMethods = "createFact")
    private void findByCustomProperty() {
        Map<String, String> properties = new HashMap<>();
        properties.put("property1", "value1");
        List<LogFact> logFacts = logFactRepository.findByCustomProperty(properties, PageRequest.of(0, 10));
        Assert.assertEquals(logFacts.size(), 1);

        properties.put("property2", "value2");
        logFacts = logFactRepository.findByCustomProperty(properties,  PageRequest.of(0, 10));
        Assert.assertEquals(logFacts.size(), 1);

        //properties = new HashMap<>();
        properties.put("property1", "value2");
        logFacts = logFactRepository.findByCustomProperty(properties,  PageRequest.of(0, 10));
        Assert.assertEquals(logFacts.size(), 0);
    }

    @Test(dependsOnMethods = "createFact")
    private void findByMultiplesValues() {
        Collection<LogFact> logFacts = logFactRepository.findBy(null, null, null, null, null, null, null, null, null,
                null, null, "property1", "value1");
        Assert.assertEquals(logFacts.size(), 1);


        logFacts = logFactRepository.findBy(null, null, null, null, null, null, null, null, null,
                null, null, "property2", "value2");
        Assert.assertEquals(logFacts.size(), 1);

        logFacts = logFactRepository.findBy(null, null, null, null, null, null, null, null, null,
                null, null, "property1", "value2");
        Assert.assertEquals(logFacts.size(), 0);
    }

    @AfterClass(alwaysRun = true)
    private void clean() {
        logFactRepository.deleteAll();
    }

}
