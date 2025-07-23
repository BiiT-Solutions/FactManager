package com.biit.factmanager.test;

import com.biit.factmanager.core.providers.ChartProvider;
import com.biit.factmanager.core.providers.FactProvider;
import com.biit.factmanager.persistence.entities.FormrunnerQuestionFact;
import com.biit.factmanager.persistence.entities.values.FormrunnerQuestionValue;
import com.biit.factmanager.persistence.enums.ChartType;
import com.jayway.jsonpath.InvalidJsonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@Test(groups = {"facts"})
public class ChartProviderTest extends AbstractTestNGSpringContextTests {

    private static final String TENANT_ID = "tenant";
    private static final String GROUP = "group";
    private static final String ELEMENT_ID = "elementId";
    private static final String SUBJECT = "tag";
    private static final String ORGANIZATION = "organization";
    private static final String PATIENT_NAME = "patient";
    private static final String EXAMINATION_VERSION = "examinationVersion";
    private static final String STRING_ANSWER = "string";
    private static final String TENANT_ID2 = "tenant2";

    private long repositorySize;

    @Autowired
    private FactProvider<FormrunnerQuestionFact> factProvider;

    @Autowired
    private ChartProvider<FormrunnerQuestionFact> chartProvider;

    private final List<FormrunnerQuestionFact> formrunnerQuestionFacts = new ArrayList<>();

    private void insertNullFactsByTenant(String tenantId) {
        for (int fact = 0; fact < 5; fact++) {
            FormrunnerQuestionValue formrunnerQuestionValue = new FormrunnerQuestionValue();
            formrunnerQuestionValue.setXpath("question" + fact);
            formrunnerQuestionValue.setAnswer(null);

            FormrunnerQuestionFact formrunnerQuestionFact = new FormrunnerQuestionFact();
            formrunnerQuestionFact.setTenant(tenantId);
            formrunnerQuestionFact.setGroup(GROUP);
            formrunnerQuestionFact.setElement(ELEMENT_ID);
            formrunnerQuestionFact.setSubject(SUBJECT);
            formrunnerQuestionFact.setOrganization(ORGANIZATION);
            formrunnerQuestionFact.setCreatedAt(LocalDateTime.now().minusMonths(fact));
            formrunnerQuestionFact.setEntity(formrunnerQuestionValue);

            factProvider.save(formrunnerQuestionFact);
        }
        repositorySize = factProvider.count();
    }

    @BeforeClass
    public void populate() {
        for (int patient = 0; patient <= 2; patient++) {
            for (int questions = 0; questions < 5; questions++) {
                FormrunnerQuestionFact formrunnerQuestionFact = new FormrunnerQuestionFact();
                FormrunnerQuestionValue formRunnerValue = new FormrunnerQuestionValue();
                formRunnerValue.setAnswer(String.valueOf((questions + patient)));
                formRunnerValue.setXpath("question" + questions);

                formrunnerQuestionFact.setTenant("persona" + patient);
                formrunnerQuestionFact.setGroup("examination" + questions);
                formrunnerQuestionFact.setElement("element" + patient);
                formrunnerQuestionFact.setEntity(formRunnerValue);

                formrunnerQuestionFacts.add(formrunnerQuestionFact);
            }
        }
        repositorySize = factProvider.count();
    }

    private String readFile(String file) throws IOException, InvalidJsonException, URISyntaxException {
        return new String(Files.readAllBytes(Paths.get(getClass().getClassLoader()
                .getResource(file).toURI())));
    }

    private void insertFactsByTenant(String tenantId) {
        for (int fact = 0; fact < 5; fact++) {
            FormrunnerQuestionValue formrunnerQuestionValue = new FormrunnerQuestionValue();
            formrunnerQuestionValue.setXpath("question" + fact);
            formrunnerQuestionValue.setAnswer(String.valueOf(fact));

            FormrunnerQuestionFact formrunnerQuestionFact = new FormrunnerQuestionFact();
            formrunnerQuestionFact.setTenant(tenantId);
            formrunnerQuestionFact.setGroup(GROUP);
            formrunnerQuestionFact.setElement(ELEMENT_ID);
            formrunnerQuestionFact.setSubject(SUBJECT);
            formrunnerQuestionFact.setOrganization(ORGANIZATION);
            formrunnerQuestionFact.setCreatedAt(LocalDateTime.now().minusMonths(fact));
            formrunnerQuestionFact.setEntity(formrunnerQuestionValue);

            factProvider.save(formrunnerQuestionFact);
        }
        repositorySize = factProvider.count();
    }

    private void insertNotNumberFacts(String tenantId) {
        for (int fact = 0; fact < 5; fact++) {
            FormrunnerQuestionValue formrunnerQuestionValue = new FormrunnerQuestionValue();
            formrunnerQuestionValue.setXpath("question" + fact);
            formrunnerQuestionValue.setItemName(PATIENT_NAME);
            formrunnerQuestionValue.setAnswer(STRING_ANSWER);

            FormrunnerQuestionFact formrunnerQuestionFact = new FormrunnerQuestionFact();
            formrunnerQuestionFact.setTenant(tenantId);
            formrunnerQuestionFact.setGroup(GROUP);
            formrunnerQuestionFact.setElement(ELEMENT_ID);
            formrunnerQuestionFact.setSubject(SUBJECT);
            formrunnerQuestionFact.setOrganization(ORGANIZATION);
            formrunnerQuestionFact.setCreatedAt(LocalDateTime.now().minusMonths(fact));
            formrunnerQuestionFact.setEntity(formrunnerQuestionValue);

            factProvider.save(formrunnerQuestionFact);
        }
        repositorySize = factProvider.count();
    }

    @Test
    public void htmlFromformrunnerQuestionFacts() throws IOException, URISyntaxException {
        Assert.assertEquals(readFile("charts/htmlFromFormrunnerQuestionFactsByTenants.html"),
                chartProvider.htmlFromformrunnerQuestionFactsByQuestion(formrunnerQuestionFacts, ChartType.BAR));
    }

    @Test(dependsOnMethods = {"htmlFromformrunnerQuestionFacts"})
    public void addNewFacts() {
        insertFactsByTenant(TENANT_ID);
        insertFactsByTenant(TENANT_ID2);
        insertNotNumberFacts(TENANT_ID);
        insertNullFactsByTenant(TENANT_ID);
        Assert.assertEquals(repositorySize, factProvider.count());
    }

    @Test(dependsOnMethods = "addNewFacts")
    public void oneTenantAllExaminations() throws IOException, URISyntaxException {
        Assert.assertEquals(readFile("charts/OneTenantMultipleExaminationsChart"), chartProvider.getChart(ORGANIZATION, null, null, null, TENANT_ID, SUBJECT, null, GROUP, ELEMENT_ID,
                null, null, null, LocalDateTime.now().minusYears(1), LocalDateTime.now().plusDays(1), null, ChartType.BAR, 0,10));
    }

    @AfterClass
    public void tearDown() {
        factProvider.getAll().forEach(fact -> factProvider.delete(fact));
    }
}
