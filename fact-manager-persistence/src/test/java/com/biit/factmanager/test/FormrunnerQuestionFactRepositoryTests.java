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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.util.Pair;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;

@SpringBootTest
@Test(groups = "factRepository")
@Rollback(false)
public class FormrunnerQuestionFactRepositoryTests extends AbstractTransactionalTestNGSpringContextTests {

    private static final String FACT_TENANT_ID = "1";
    private static final long FACT_EXAMINATION_ID = 2;
    private static final long FACT_COMPANY_ID = 3;
    private static final long FACT_PATIENT_ID = 4;
    private static final long FACT_PROFESSIONAL_ID = 5;
    private static final String FACT_QUESTION_XPATH = "/form/Category/Question";
    private static final String FACT_QUESTION_XPATH_2 = "/form/Category/Question2";
    private static final String FACT_ANSWER = "Answer";
    private static final String FACT_EXAMINATION_NAME = "test";
    private static final String FACT_ELEMENT_ID = "elementId";
    private static final String FACT_CATEGORY = "category";
    private static final LocalDateTime FACT_DATE_BEFORE = LocalDateTime.now().minusDays(20);
    private static final LocalDateTime FACT_DATE_AFTER = LocalDateTime.now().plusDays(20);
    private static final LocalDateTime FACT_DATE_NOW = LocalDateTime.now();
    private static final String ITEM_NAME = "item";
    private static final String FORM_NAME = "form";
    private static final String FORM_VERSION = "1";

    private long repositorySize;
    @Autowired
    private FactRepository<FormrunnerQuestionFact> formrunnerQuestionFactRepository;


    @BeforeClass
    private void populate() {
        repositorySize = formrunnerQuestionFactRepository.count();
        FormrunnerQuestionFact createdAtBeforeAndTenantIdAndCategory = new FormrunnerQuestionFact();
        FormrunnerQuestionFact createdAtNowAndTenantIdAndElementId = new FormrunnerQuestionFact();
        FormrunnerQuestionFact createdAtAfterAndElementIdAndCategory = new FormrunnerQuestionFact();

        createdAtBeforeAndTenantIdAndCategory.setTenant(FACT_TENANT_ID);
        createdAtBeforeAndTenantIdAndCategory.setGroup(FACT_CATEGORY);
        createdAtBeforeAndTenantIdAndCategory.setCreatedAt(FACT_DATE_BEFORE);
        createdAtNowAndTenantIdAndElementId.setCreatedAt(FACT_DATE_AFTER);
        createdAtNowAndTenantIdAndElementId.setTenant(FACT_TENANT_ID);
        createdAtNowAndTenantIdAndElementId.setElement(FACT_ELEMENT_ID);
        createdAtAfterAndElementIdAndCategory.setCreatedAt(FACT_DATE_AFTER);
        createdAtAfterAndElementIdAndCategory.setElement(FACT_ELEMENT_ID);
        createdAtAfterAndElementIdAndCategory.setGroup(FACT_CATEGORY);

        formrunnerQuestionFactRepository.save(createdAtBeforeAndTenantIdAndCategory);
        formrunnerQuestionFactRepository.save(createdAtNowAndTenantIdAndElementId);
        formrunnerQuestionFactRepository.save(createdAtAfterAndElementIdAndCategory);
        repositorySize += 3;
    }

    @Test
    private void getAllAtTheBeginning() {
        Assert.assertEquals(formrunnerQuestionFactRepository.count(), repositorySize);
    }

    @Test(dependsOnMethods = "getAllAtTheBeginning")
    private void addFactsWithValues() {
        FormrunnerQuestionFact factToSave = new FormrunnerQuestionFact();
        factToSave.setGroup(FACT_EXAMINATION_NAME);
        factToSave.setElement(FACT_EXAMINATION_ID + "");
        factToSave.setTenant(FACT_PATIENT_ID + "");

        FormrunnerQuestionValue formrunnerQuestionValue = new FormrunnerQuestionValue();
        formrunnerQuestionValue.setCompanyId(FACT_COMPANY_ID);
        formrunnerQuestionValue.setProfessionalId(FACT_PROFESSIONAL_ID);
        formrunnerQuestionValue.setXpath(FACT_QUESTION_XPATH);
        formrunnerQuestionValue.setAnswer(FACT_ANSWER);
        formrunnerQuestionValue.setItemName(ITEM_NAME);
        formrunnerQuestionValue.setFormName(FORM_NAME);
        formrunnerQuestionValue.setFormVersion(FORM_VERSION);
        Assert.assertEquals(formrunnerQuestionValue.getQuestion(), "Question");
        factToSave.setEntity(formrunnerQuestionValue);

        formrunnerQuestionFactRepository.save(factToSave);
        Assert.assertEquals(formrunnerQuestionFactRepository.count(), repositorySize += 1);

        factToSave = new FormrunnerQuestionFact();
        factToSave.setGroup(FACT_EXAMINATION_NAME);
        factToSave.setElement(FACT_EXAMINATION_ID + "");
        factToSave.setTenant(FACT_PATIENT_ID + "");

        formrunnerQuestionValue = new FormrunnerQuestionValue();
        formrunnerQuestionValue.setCompanyId(FACT_COMPANY_ID);
        formrunnerQuestionValue.setProfessionalId(FACT_PROFESSIONAL_ID);
        formrunnerQuestionValue.setXpath(FACT_QUESTION_XPATH_2);
        formrunnerQuestionValue.setAnswer(FACT_ANSWER);
        Assert.assertEquals(formrunnerQuestionValue.getQuestion(), "Question2");
        factToSave.setEntity(formrunnerQuestionValue);

        formrunnerQuestionFactRepository.save(factToSave);
        Assert.assertEquals(formrunnerQuestionFactRepository.count(), repositorySize += 1);

    }

    @Test(dependsOnMethods = "addFactsWithValues")
    private void searchFactByValueCompany() {
        Collection<FormrunnerQuestionFact> facts = formrunnerQuestionFactRepository.findByValueParameters(PageRequest.of(0, 10), Pair.of("companyId", FACT_COMPANY_ID));
        Assert.assertEquals(facts.size(), 2);
    }

    @Test(dependsOnMethods = "searchFactByValueCompany")
    private void searchFactByValueQuestion() {
        Collection<FormrunnerQuestionFact> facts = formrunnerQuestionFactRepository.findByValueParameters(PageRequest.of(0, 10), Pair.of("xpath", FACT_QUESTION_XPATH));
        Assert.assertEquals(facts.size(), 1);
    }

    @Test(dependsOnMethods = "searchFactByValueCompany")
    private void searchFactByValueAnswer() {
        Collection<FormrunnerQuestionFact> facts = formrunnerQuestionFactRepository.findByValueParameters(PageRequest.of(0, 10), Pair.of("answer", FACT_ANSWER));
        Assert.assertEquals(facts.size(), 2);
    }

    @Test(dependsOnMethods = "searchFactByValueCompany")
    private void searchFactByValueQuestionAndAnswer() {
        Collection<FormrunnerQuestionFact> facts = formrunnerQuestionFactRepository.findByValueParameters(PageRequest.of(0, 10), Pair.of("xpath", FACT_QUESTION_XPATH),
                Pair.of("answer", FACT_ANSWER));
        Assert.assertEquals(facts.size(), 1);
    }

    @Test(dependsOnMethods = "searchFactByValueCompany")
    private void searchFactByInvalidValueQuestionAndAnswer() {
        Collection<FormrunnerQuestionFact> facts = formrunnerQuestionFactRepository.findByValueParameters(PageRequest.of(0, 10), Pair.of("xpathWrong", FACT_QUESTION_XPATH),
                Pair.of("answer", FACT_ANSWER));
        Assert.assertEquals(facts.size(), 0);
    }

    @Test(dependsOnMethods = "searchFactByValueCompany")
    private void searchFactByValueQuestionAndAnswerValidDate() {
        Collection<FormrunnerQuestionFact> facts = formrunnerQuestionFactRepository.findBy(null, null, null, null, null, null, null, null, null, null, null, null, LocalDateTime.now().minus(1, ChronoUnit.HOURS),
                LocalDateTime.now(), null, null, null, PageRequest.of(0, 10), Pair.of("xpath", FACT_QUESTION_XPATH), Pair.of("answer", FACT_ANSWER));
        Assert.assertEquals(facts.size(), 1);
    }

    @Test(dependsOnMethods = "searchFactByValueCompany")
    private void searchFactByValueQuestionAndAnswerInvalidDate() {
        Collection<FormrunnerQuestionFact> facts = formrunnerQuestionFactRepository.findBy(null, null, null, null, null, null, null, null, null, null, null, null, LocalDateTime.now(),
                LocalDateTime.now(), null, null, null, PageRequest.of(0, 10), Pair.of("question", FACT_QUESTION_XPATH), Pair.of("answer", FACT_ANSWER));
        Assert.assertEquals(facts.size(), 0);
    }


    @Test(dependsOnMethods = "addFactsWithValues")
    private void getFilteredFacts() {
        Assert.assertEquals(formrunnerQuestionFactRepository.count(), repositorySize);
        Assert.assertEquals((long) formrunnerQuestionFactRepository.findByTenantAndGroupAndCreatedAt
                (FACT_TENANT_ID, FACT_CATEGORY, FACT_DATE_BEFORE.minusDays(1), FACT_DATE_AFTER).size(), 1);
        Assert.assertEquals((long) formrunnerQuestionFactRepository.findByTenantIdAndElementAndCreatedAt
                (FACT_TENANT_ID, FACT_ELEMENT_ID, FACT_DATE_BEFORE, FACT_DATE_AFTER).size(), 1);
        Assert.assertEquals((long) formrunnerQuestionFactRepository.findByElementAndGroupAndCreatedAt
                (FACT_ELEMENT_ID, FACT_CATEGORY, FACT_DATE_NOW, FACT_DATE_AFTER.plusDays(1)).size(), 1);
    }

    @Test(dependsOnMethods = "getFilteredFacts")
    private void factBetweenDates() {
        Assert.assertEquals(formrunnerQuestionFactRepository.findByCreatedAtGreaterThanEqualAndCreatedAtLessThanEqual
                (LocalDateTime.now().minusDays(21), LocalDateTime.now().plusDays(21)).size(), 5);
    }

    @Test(dependsOnMethods = "factBetweenDates")
    private void factBeforeDate() {
        Assert.assertEquals(formrunnerQuestionFactRepository.findByCreatedAtLessThan
                (LocalDateTime.now().plusDays(21)).size(), repositorySize);
    }

    @Test(dependsOnMethods = "factBeforeDate")
    private void factAfterDate() {
        Assert.assertEquals(formrunnerQuestionFactRepository.findByCreatedAtGreaterThan
                (FACT_DATE_NOW).size(), 5);
    }

    @AfterClass(alwaysRun = true)
    private void deleteFact() {
        formrunnerQuestionFactRepository.deleteAll();
        Assert.assertEquals(formrunnerQuestionFactRepository.count(), 0);
    }
}
