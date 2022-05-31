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
    private static final String TAG = "tag";
    private static final String ORGANIZATION_ID = "organizationId";
    private static final String PATIENT_NAME = "patient";
    private static final String EXAMINATION_VERSION = "examinationVersion";

    @Autowired
    private FactProvider<FormrunnerQuestionFact> factProvider;

    @Autowired
    private ChartProvider<FormrunnerQuestionFact> chartProvider;

    private final List<FormrunnerQuestionFact> formrunnerQuestionFacts = new ArrayList<>();

    @BeforeClass
    public void populate() {
        for (int patient = 0; patient <= 2; patient++) {
            for (int questions = 0; questions < 5; questions++) {
                FormrunnerQuestionFact formrunnerQuestionFact = new FormrunnerQuestionFact();
                FormrunnerQuestionValue formRunnerValue = new FormrunnerQuestionValue();

                formRunnerValue.setScore((double) (questions + patient));
                formRunnerValue.setQuestion("question" + questions);

                formrunnerQuestionFact.setTenantId("persona" + patient);
                formrunnerQuestionFact.setGroup("examination" + questions);
                formrunnerQuestionFact.setElementId("element" + patient);
                formrunnerQuestionFact.setEntity(formRunnerValue);

                formrunnerQuestionFacts.add(formrunnerQuestionFact);
            }
        }
    }

    @Test
    public void htmlFromformrunnerQuestionFacts() throws IOException, URISyntaxException {
        Assert.assertEquals(readFile("charts/htmlFromFormrunnerQuestionFactsByTenants.html"),
                chartProvider.htmlFromformrunnerQuestionFactsByQuestion(formrunnerQuestionFacts, ChartType.BAR));
    }

    @Test(dependsOnMethods = {"htmlFromformrunnerQuestionFacts"})
    public void addNewFacts() {
        insertFactsByTenant(TENANT_ID);
        insertFactsByTenant("patata");
        insertNotNumberFacts(TENANT_ID);
        Assert.assertTrue(factProvider.getAll().size() == 15);
    }

    @Test(dependsOnMethods = "addNewFacts")
    public void oneTenantAllExaminations() throws IOException, URISyntaxException {
        Assert.assertEquals(readFile("charts/OneTenantMultipleExaminationsChart"), chartProvider.getChart(ORGANIZATION_ID, TENANT_ID, TAG, GROUP, ELEMENT_ID,
                LocalDateTime.now().minusYears(1), LocalDateTime.now().plusDays(1), null, ChartType.BAR));
    }

    private String readFile(String file) throws IOException, InvalidJsonException, URISyntaxException {
        return new String(Files.readAllBytes(Paths.get(getClass().getClassLoader()
                .getResource(file).toURI())));
    }

    private void insertFactsByTenant(String tenantId) {
        for (int fact = 0; fact < 5; fact++) {
            FormrunnerQuestionValue formrunnerQuestionValue = new FormrunnerQuestionValue();
            formrunnerQuestionValue.setQuestion("question"+ fact);
            formrunnerQuestionValue.setScore((double) fact);
            formrunnerQuestionValue.setExaminationVersion(EXAMINATION_VERSION);
            formrunnerQuestionValue.setPatientName(PATIENT_NAME);

            FormrunnerQuestionFact formrunnerQuestionFact = new FormrunnerQuestionFact();
            formrunnerQuestionFact.setTenantId(tenantId);
            formrunnerQuestionFact.setGroup(GROUP);
            formrunnerQuestionFact.setElementId(ELEMENT_ID);
            formrunnerQuestionFact.setTag(TAG);
            formrunnerQuestionFact.setOrganizationId(ORGANIZATION_ID);
            formrunnerQuestionFact.setCreatedAt(LocalDateTime.now().minusMonths(fact));
            formrunnerQuestionFact.setEntity(formrunnerQuestionValue);

            factProvider.save(formrunnerQuestionFact);
        }
    }

    private void insertNotNumberFacts(String tenantId) {
        for (int fact = 0; fact < 5; fact++) {
            FormrunnerQuestionValue formrunnerQuestionValue = new FormrunnerQuestionValue();
            formrunnerQuestionValue.setQuestion("question"+ fact);
            formrunnerQuestionValue.setScore(null);
            formrunnerQuestionValue.setExaminationVersion(EXAMINATION_VERSION);
            formrunnerQuestionValue.setPatientName(PATIENT_NAME);

            FormrunnerQuestionFact formrunnerQuestionFact = new FormrunnerQuestionFact();
            formrunnerQuestionFact.setTenantId(tenantId);
            formrunnerQuestionFact.setGroup(GROUP);
            formrunnerQuestionFact.setElementId(ELEMENT_ID);
            formrunnerQuestionFact.setTag(TAG);
            formrunnerQuestionFact.setOrganizationId(ORGANIZATION_ID);
            formrunnerQuestionFact.setCreatedAt(LocalDateTime.now().minusMonths(fact));
            formrunnerQuestionFact.setEntity(formrunnerQuestionValue);

            factProvider.save(formrunnerQuestionFact);
        }
    }

    @AfterClass
    public void tearDown() {
        factProvider.getAll().forEach(fact -> factProvider.delete(fact));
    }
}
