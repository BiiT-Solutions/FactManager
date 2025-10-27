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

import com.biit.factmanager.persistence.entities.FormrunnerQuestionFact;
import com.biit.factmanager.persistence.entities.values.FormrunnerQuestionValue;
import com.biit.factmanager.persistence.repositories.FactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@SpringBootTest
@Test(groups = "factRepository")
@Rollback(false)
public class FactValueTests extends AbstractTransactionalTestNGSpringContextTests {

    @Autowired
    private FactRepository<FormrunnerQuestionFact> formrunnerQuestionFactRepository;

    @BeforeClass
    public void populate() {
        formrunnerQuestionFactRepository.deleteAll();
        for (int i = 0; i < 10; i++) {
            FormrunnerQuestionValue formrunnerQuestionValue = new FormrunnerQuestionValue();
            formrunnerQuestionValue.setAnswer("answer" + i);
            formrunnerQuestionValue.setCompanyId((long) i);
            formrunnerQuestionValue.setProfessionalId((long) i);

            FormrunnerQuestionFact formrunnerQuestionFact = new FormrunnerQuestionFact();
            formrunnerQuestionFact.setSubject("tag" + i);
            formrunnerQuestionFact.setSession("" + i);
            formrunnerQuestionFact.setGroup("group" + i);
            formrunnerQuestionFact.setElement("elementId" + i);
            formrunnerQuestionFact.setTenant("tenantId" + i);
            formrunnerQuestionFact.setOrganization("organization" + i);
            formrunnerQuestionFact.setEntity(formrunnerQuestionValue);
            Long id = formrunnerQuestionFactRepository.save(formrunnerQuestionFact).getId();
            FormrunnerQuestionFact formrunnerQuestionFactDB = formrunnerQuestionFactRepository.findById(id).orElse(null);
            Assert.assertNotNull(formrunnerQuestionFactDB);
        }
    }

    @Test
    public void valueNotNull() {
        formrunnerQuestionFactRepository.findAll().forEach(formrunnerQuestionFact -> {
            Assert.assertNotNull(formrunnerQuestionFact.getValue());
            Assert.assertNotNull(formrunnerQuestionFact.getEntity().getQuestion());
        });
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        formrunnerQuestionFactRepository.deleteAll();
    }
}
