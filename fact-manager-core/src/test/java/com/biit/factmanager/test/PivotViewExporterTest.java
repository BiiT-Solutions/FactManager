package com.biit.factmanager.test;

import com.biit.factmanager.core.providers.PivotViewProvider;
import com.biit.factmanager.persistence.entities.Fact;
import com.biit.factmanager.persistence.entities.values.FormRunnerValue;
import com.biit.factmanager.persistence.entities.FormRunnerFact;
import com.biit.factmanager.persistence.repositories.FormRunnerFactRepository;
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
    private FormRunnerFactRepository formrunnerFactRepository;

    @Autowired
    private PivotViewProvider<FormRunnerValue, FormRunnerFact> pivotViewProvider;

    private final List<FormRunnerFact> formRunnerFacts = new ArrayList<>();

    @BeforeClass
    public void populate() {
        //5 Patients
        for (int patient = 0; patient < 5; patient++) {
            // 2 Examinations
            for (int examination = 0; examination < 2; examination++) {
                // Each Examination with 10 questions
                for (int question = 0; question < 10; question++) {
                    FormRunnerFact formrunnerFact = new FormRunnerFact();
                    formrunnerFact.setGroup("examination" + examination);
                    formrunnerFact.setElementId("examinationId" + examination);
                    formrunnerFact.setTag("tag");
                    formrunnerFact.setTenantId("p" + patient);

                    FormRunnerValue formRunnerValue = new FormRunnerValue();
                    formRunnerValue.setQuestion(String.format("Question_%s_%s", examination, question));
                    formRunnerValue.setScore(Double.parseDouble("0." + question));
                    formRunnerValue.setPatientName("Patient" + patient);
                    formrunnerFact.setEntity(formRunnerValue);

                    formrunnerFact = formrunnerFactRepository.save(formrunnerFact);
                    formRunnerFacts.add(formrunnerFact);
                }
            }
        }
        //Set Item image (score).

    }

    @Test
    public void xmlFromFormRunnerFact() throws IOException, URISyntaxException {
        Assert.assertEquals(pivotViewProvider.xmlFromTenants(formRunnerFacts), readFile("pivotviewer/formRunnerFacts.xml"));
    }

    private FormRunnerValue getFormRunnerValueFromJson(Fact<FormRunnerValue> fact) throws JsonProcessingException, JSONException {
        ObjectMapper objectMapper = new ObjectMapper();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("score", fact.getValue());
        jsonObject.put("group", fact.getGroup());
        return objectMapper.readValue(jsonObject.toString(), FormRunnerValue.class);
    }

    @AfterClass
    public void cleanUp() {
        for (FormRunnerFact fact : formRunnerFacts) {
            formrunnerFactRepository.delete(fact);
        }
    }

    private String readFile(String file) throws IOException, InvalidJsonException, URISyntaxException {
        return new String(Files.readAllBytes(Paths.get(getClass().getClassLoader()
                .getResource(file).toURI())));
    }
}
