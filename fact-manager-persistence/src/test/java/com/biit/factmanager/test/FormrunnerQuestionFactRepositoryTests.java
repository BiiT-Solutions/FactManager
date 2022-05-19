package com.biit.factmanager.test;

import com.biit.factmanager.persistence.entities.FormrunnerQuestionFact;
import com.biit.factmanager.persistence.entities.values.FormrunnerQuestionValue;
import com.biit.factmanager.persistence.repositories.FactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
    private static final String FACT_QUESTION = "Question";
    private static final String FACT_QUESTION_2 = "Question2";
    private static final String FACT_ANSWER = "Answer";
    private static final String FACT_EXAMINATION_NAME = "test";
    private static final String FACT_ELEMENT_ID = "elementId";
    private static final String FACT_CATEGORY = "category";
    private static final LocalDateTime FACT_DATE_BEFORE = LocalDateTime.now().minusDays(20);
    private static final LocalDateTime FACT_DATE_AFTER = LocalDateTime.now().plusDays(20);
    private static final LocalDateTime FACT_DATE_NOW = LocalDateTime.now();
    private int repositorySize;

    @Autowired
    private FactRepository<FormrunnerQuestionFact> formrunnerQuestionFactRepository;


    @BeforeClass
    private void populate() {
        repositorySize = formrunnerQuestionFactRepository.findAll().size();

        FormrunnerQuestionFact createdAtBeforeAndTenantIdAndCategory = new FormrunnerQuestionFact();
        createdAtBeforeAndTenantIdAndCategory.setTenantId(FACT_TENANT_ID);
        createdAtBeforeAndTenantIdAndCategory.setGroup(FACT_CATEGORY);
        createdAtBeforeAndTenantIdAndCategory.setCreatedAt(FACT_DATE_BEFORE);
        formrunnerQuestionFactRepository.save(createdAtBeforeAndTenantIdAndCategory);

        FormrunnerQuestionFact createdAtNowAndTenantIdAndElementId = new FormrunnerQuestionFact();
        createdAtNowAndTenantIdAndElementId.setCreatedAt(FACT_DATE_AFTER);
        createdAtNowAndTenantIdAndElementId.setTenantId(FACT_TENANT_ID);
        createdAtNowAndTenantIdAndElementId.setElementId(FACT_ELEMENT_ID);
        formrunnerQuestionFactRepository.save(createdAtNowAndTenantIdAndElementId);

        FormrunnerQuestionFact createdAtAfterAndElementIdAndCategory = new FormrunnerQuestionFact();
        createdAtAfterAndElementIdAndCategory.setCreatedAt(FACT_DATE_AFTER);
        createdAtAfterAndElementIdAndCategory.setElementId(FACT_ELEMENT_ID);
        createdAtAfterAndElementIdAndCategory.setGroup(FACT_CATEGORY);
        formrunnerQuestionFactRepository.save(createdAtAfterAndElementIdAndCategory);

    }

    @Test
    private void getAllAtTheBeginning() {

        Assert.assertEquals(formrunnerQuestionFactRepository.count(), repositorySize += 3);
    }

    @Test(dependsOnMethods = "getAllAtTheBeginning")
    private void addFactsWithValues() {
        FormrunnerQuestionFact factToSave = new FormrunnerQuestionFact();
        factToSave.setGroup(FACT_EXAMINATION_NAME);
        factToSave.setElementId(FACT_EXAMINATION_ID + "");
        factToSave.setTenantId(FACT_PATIENT_ID + "");

        FormrunnerQuestionValue FormrunnerQuestionValue = new FormrunnerQuestionValue();
        FormrunnerQuestionValue.setCompanyId(FACT_COMPANY_ID);
        FormrunnerQuestionValue.setProfessionalId(FACT_PROFESSIONAL_ID);
        FormrunnerQuestionValue.setQuestion(FACT_QUESTION);
        FormrunnerQuestionValue.setAnswer(FACT_ANSWER);
        factToSave.setEntity(FormrunnerQuestionValue);

        formrunnerQuestionFactRepository.save(factToSave);
        Assert.assertEquals(formrunnerQuestionFactRepository.count(), repositorySize += 1);

        factToSave = new FormrunnerQuestionFact();
        factToSave.setGroup(FACT_EXAMINATION_NAME);
        factToSave.setElementId(FACT_EXAMINATION_ID + "");
        factToSave.setTenantId(FACT_PATIENT_ID + "");

        FormrunnerQuestionValue = new FormrunnerQuestionValue();
        FormrunnerQuestionValue.setCompanyId(FACT_COMPANY_ID);
        FormrunnerQuestionValue.setProfessionalId(FACT_PROFESSIONAL_ID);
        FormrunnerQuestionValue.setQuestion(FACT_QUESTION_2);
        FormrunnerQuestionValue.setAnswer(FACT_ANSWER);
        factToSave.setEntity(FormrunnerQuestionValue);

        formrunnerQuestionFactRepository.save(factToSave);
        Assert.assertEquals(formrunnerQuestionFactRepository.count(), repositorySize += 1);

    }

    @Test(dependsOnMethods = "addFactsWithValues")
    private void searchFactByValueCompany() {
        Collection<FormrunnerQuestionFact> facts = formrunnerQuestionFactRepository.findByValueParameters(Pair.of("companyId", FACT_COMPANY_ID));
        Assert.assertEquals(facts.size(), 2);
    }

    @Test(dependsOnMethods = "searchFactByValueCompany")
    private void searchFactByValueQuestion() {
        Collection<FormrunnerQuestionFact> facts = formrunnerQuestionFactRepository.findByValueParameters(Pair.of("question", FACT_QUESTION));
        Assert.assertEquals(facts.size(), 1);
    }

    @Test(dependsOnMethods = "searchFactByValueCompany")
    private void searchFactByValueAnswer() {
        Collection<FormrunnerQuestionFact> facts = formrunnerQuestionFactRepository.findByValueParameters(Pair.of("answer", FACT_ANSWER));
        Assert.assertEquals(facts.size(), 2);
    }

    @Test(dependsOnMethods = "searchFactByValueCompany")
    private void searchFactByValueQuestionAndAnswer() {
        Collection<FormrunnerQuestionFact> facts = formrunnerQuestionFactRepository.findByValueParameters(Pair.of("question", FACT_QUESTION),
                Pair.of("answer", FACT_ANSWER));
        Assert.assertEquals(facts.size(), 1);
    }

    @Test(dependsOnMethods = "searchFactByValueCompany")
    private void searchFactByInvalidValueQuestionAndAnswer() {
        Collection<FormrunnerQuestionFact> facts = formrunnerQuestionFactRepository.findByValueParameters(Pair.of("questionA", FACT_QUESTION),
                Pair.of("answer", FACT_ANSWER));
        Assert.assertEquals(facts.size(), 0);
    }

    @Test(dependsOnMethods = "searchFactByValueCompany")
    private void searchFactByValueQuestionAndAnswerValidDate() {
        Collection<FormrunnerQuestionFact> facts = formrunnerQuestionFactRepository.findBy(null, null, null, null, null, LocalDateTime.now().minus(1, ChronoUnit.HOURS),
                LocalDateTime.now(), Pair.of("question", FACT_QUESTION), Pair.of("answer", FACT_ANSWER));
        Assert.assertEquals(facts.size(), 1);
    }

    @Test(dependsOnMethods = "searchFactByValueCompany")
    private void searchFactByValueQuestionAndAnswerInvalidDate() {
        Collection<FormrunnerQuestionFact> facts = formrunnerQuestionFactRepository.findBy(null, null, null, null, null, LocalDateTime.now(),
                LocalDateTime.now(), Pair.of("question", FACT_QUESTION), Pair.of("answer", FACT_ANSWER));
        Assert.assertEquals(facts.size(), 0);
    }


    @Test(dependsOnMethods = "addFactsWithValues")
    private void getFilteredFacts() {
        Assert.assertEquals(formrunnerQuestionFactRepository.count(), repositorySize);
        Assert.assertEquals((long) formrunnerQuestionFactRepository.findByTenantIdAndGroupAndCreatedAt
                (FACT_TENANT_ID, FACT_CATEGORY, FACT_DATE_BEFORE.minusDays(1), FACT_DATE_AFTER).size(), 1);
        Assert.assertEquals((long) formrunnerQuestionFactRepository.findByTenantIdAndElementIdAndCreatedAt
                (FACT_TENANT_ID, FACT_ELEMENT_ID, FACT_DATE_BEFORE, FACT_DATE_AFTER).size(), 1);
        Assert.assertEquals((long) formrunnerQuestionFactRepository.findByElementIdAndGroupAndCreatedAt
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
                (FACT_DATE_NOW).size(), 4);
    }

    @AfterClass
    private void deleteFact() {
        formrunnerQuestionFactRepository.deleteAll();
        Assert.assertEquals(formrunnerQuestionFactRepository.count(), 0);
    }
}
