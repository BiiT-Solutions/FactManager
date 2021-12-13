package com.biit.factmanager.test;

import com.biit.factmanager.core.providers.FactProvider;
import com.biit.factmanager.core.providers.PivotViewProvider;
import com.biit.factmanager.persistence.entities.Fact;
import com.biit.factmanager.persistence.entities.values.FormRunnerValue;
import com.biit.factmanager.persistence.entities.FormRunnerFact;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.InvalidJsonException;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@Rollback(value = false)
@Test(groups = {"pivotViewExporter"})
public class PivotViewExporterTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private FactProvider<FormRunnerValue, FormRunnerFact> factProvider;

    @Autowired
    private PivotViewProvider<FormRunnerValue, FormRunnerFact> pivotViewProvider;

    private final List<FormRunnerFact> formRunnerFacts = new ArrayList<>();

    @BeforeClass
    public void populate() {
        //5 Patients
        for (int patient = 0; patient < 5; patient++) {
            // 2 Examinations
            for (int examination = 1; examination < 3; examination++) {
                String formXpath = "/";
                //Set form scores (image index).
                FormRunnerFact formFormRunnerFact = new FormRunnerFact();
                formFormRunnerFact.setGroup("examination" + examination);
                formFormRunnerFact.setElementId("examinationId" + examination);
                formFormRunnerFact.setTenantId("p" + patient);

                //Forms scores has not question field filled up.
                FormRunnerValue formFormRunnerValue = new FormRunnerValue();
                formFormRunnerValue.setScore((double) examination);
                formFormRunnerValue.setXpath(formXpath);
                formFormRunnerValue.setPatientName("Patient" + patient);
                formFormRunnerFact.setEntity(formFormRunnerValue);
                formFormRunnerFact = factProvider.save(formFormRunnerFact);
                formRunnerFacts.add(formFormRunnerFact);

                // Each examination 2 categories
                for (int category = 1; category < 3; category++) {
                    String categoryXpath = formXpath + "category" + category + "/";
                    //Set category.
                    FormRunnerFact categoryFormRunnerFact = new FormRunnerFact();
                    categoryFormRunnerFact.setGroup("examination" + examination);
                    categoryFormRunnerFact.setElementId("examinationId" + examination);
                    categoryFormRunnerFact.setTenantId("p" + patient);

                    //Categories scores has not question field filled up.
                    FormRunnerValue categoryFormRunnerValue = new FormRunnerValue();
                    categoryFormRunnerValue.setScore((double) category);
                    categoryFormRunnerValue.setParent("form");
                    categoryFormRunnerValue.setXpath(categoryXpath);
                    categoryFormRunnerValue.setPatientName("Patient" + patient);
                    categoryFormRunnerFact.setEntity(categoryFormRunnerValue);

                    categoryFormRunnerFact = factProvider.save(categoryFormRunnerFact);
                    formRunnerFacts.add(categoryFormRunnerFact);
                    // Each Examination with 10 questions
                    for (int question = 0; question < 10; question++) {
                        String questionXpath = categoryXpath + String.format("Question_%s_%s_%s", examination, category, question) + "/";
                        FormRunnerFact questionFormRunnerFact = new FormRunnerFact();
                        questionFormRunnerFact.setGroup("examination" + examination);
                        questionFormRunnerFact.setElementId("examinationId" + examination);
                        questionFormRunnerFact.setTag("tag");
                        questionFormRunnerFact.setTenantId("p" + patient);

                        FormRunnerValue questionFormRunnerValue = new FormRunnerValue();
                        questionFormRunnerValue.setQuestion(String.format("Question_%s_%s_%s", examination, category, question));
                        questionFormRunnerValue.setParent("category" + category);
                        questionFormRunnerValue.setScore(Double.parseDouble("0." + question));
                        questionFormRunnerValue.setXpath(questionXpath);
                        questionFormRunnerValue.setPatientName("Patient" + patient);
                        questionFormRunnerFact.setEntity(questionFormRunnerValue);

                        questionFormRunnerFact = factProvider.save(questionFormRunnerFact);
                        formRunnerFacts.add(questionFormRunnerFact);
                    }
                }
            }
        }
    }

    @Test
    public void xmlFromFormRunnerFact() throws IOException, URISyntaxException {
        Assert.assertEquals(pivotViewProvider.xmlFromTenants(formRunnerFacts), readFile("pivotviewer/formRunnerFacts.xml"));
    }

    private FormRunnerValue getFormRunnerValueFromJson(Fact<FormRunnerValue> fact) throws JsonProcessingException, JSONException {
        ObjectMapper objectMapper = new ObjectMapper();
        JSONObject jsonObject = new JSONObject();
        //jsonObject.put("score", fact.getValue());
        jsonObject.put("group", fact.getGroup());
        return objectMapper.readValue(jsonObject.toString(), FormRunnerValue.class);
    }

    @AfterClass
    public void cleanUp() {
        for (FormRunnerFact fact : formRunnerFacts) {
            factProvider.delete(fact);
        }
    }

    private String readFile(String file) throws IOException, InvalidJsonException, URISyntaxException {
        return new String(Files.readAllBytes(Paths.get(getClass().getClassLoader()
                .getResource(file).toURI())));
    }
}
