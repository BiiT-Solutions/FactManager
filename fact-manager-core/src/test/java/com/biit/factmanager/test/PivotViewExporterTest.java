package com.biit.factmanager.test;

import com.biit.factmanager.core.providers.FactProvider;
import com.biit.factmanager.core.providers.PivotViewProvider;
import com.biit.factmanager.persistence.entities.FormrunnerQuestionFact;
import com.biit.factmanager.persistence.entities.values.FormrunnerQuestionValue;
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
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@Test(groups = {"pivotViewExporter"})
public class PivotViewExporterTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private FactProvider<FormrunnerQuestionFact> factProvider;

    @Autowired
    private PivotViewProvider<FormrunnerQuestionFact> pivotViewProvider;

    private final List<FormrunnerQuestionFact> FormrunnerQuestionFacts = new ArrayList<>();

    @BeforeClass
    public void populate() {
        //5 Patients
        for (int patient = 0; patient < 5; patient++) {
            // 2 Examinations
            for (int examination = 1; examination < 3; examination++) {
                String formXpath = "/";
                //Set form scores (image index).
                FormrunnerQuestionFact formFormrunnerQuestionFact = new FormrunnerQuestionFact();
                formFormrunnerQuestionFact.setGroup("examination" + examination);
                formFormrunnerQuestionFact.setElementId("examinationId" + examination);
                formFormrunnerQuestionFact.setTenantId("p" + patient);

                //Forms scores has not question field filled up.
                FormrunnerQuestionValue formFormrunnerQuestionValue = new FormrunnerQuestionValue();
                formFormrunnerQuestionValue.setScore((double) examination);
                formFormrunnerQuestionValue.setXpath(formXpath);
                formFormrunnerQuestionValue.setPatientName("Patient" + patient);
                formFormrunnerQuestionFact.setEntity(formFormrunnerQuestionValue);
                formFormrunnerQuestionFact = factProvider.save(formFormrunnerQuestionFact);
                FormrunnerQuestionFacts.add(formFormrunnerQuestionFact);

                // Each examination 2 categories
                for (int category = 1; category < 3; category++) {
                    String categoryXpath = formXpath + "category" + category + "/";
                    //Set category.
                    FormrunnerQuestionFact categoryFormrunnerQuestionFact = new FormrunnerQuestionFact();
                    categoryFormrunnerQuestionFact.setGroup("examination" + examination);
                    categoryFormrunnerQuestionFact.setElementId("examinationId" + examination);
                    categoryFormrunnerQuestionFact.setTenantId("p" + patient);

                    //Categories scores has not question field filled up.
                    FormrunnerQuestionValue categoryFormrunnerQuestionValue = new FormrunnerQuestionValue();
                    categoryFormrunnerQuestionValue.setScore((double) category);
                    categoryFormrunnerQuestionValue.setParent("form");
                    categoryFormrunnerQuestionValue.setXpath(categoryXpath);
                    categoryFormrunnerQuestionValue.setPatientName("Patient" + patient);
                    categoryFormrunnerQuestionFact.setEntity(categoryFormrunnerQuestionValue);

                    categoryFormrunnerQuestionFact = factProvider.save(categoryFormrunnerQuestionFact);
                    FormrunnerQuestionFacts.add(categoryFormrunnerQuestionFact);
                    // Each Examination with 10 questions
                    for (int question = 0; question < 10; question++) {
                        String questionXpath = categoryXpath + String.format("Question_%s_%s_%s", examination, category, question) + "/";
                        FormrunnerQuestionFact questionFormrunnerQuestionFact = new FormrunnerQuestionFact();
                        questionFormrunnerQuestionFact.setGroup("examination" + examination);
                        questionFormrunnerQuestionFact.setElementId("examinationId" + examination);
                        questionFormrunnerQuestionFact.setTag("tag");
                        questionFormrunnerQuestionFact.setTenantId("p" + patient);

                        FormrunnerQuestionValue questionFormrunnerQuestionValue = new FormrunnerQuestionValue();
                        questionFormrunnerQuestionValue.setQuestion(String.format("Question_%s_%s_%s", examination, category, question));
                        questionFormrunnerQuestionValue.setParent("category" + category);
                        questionFormrunnerQuestionValue.setScore(Double.parseDouble("0." + question));
                        questionFormrunnerQuestionValue.setXpath(questionXpath);
                        questionFormrunnerQuestionValue.setPatientName("Patient" + patient);
                        questionFormrunnerQuestionFact.setEntity(questionFormrunnerQuestionValue);

                        questionFormrunnerQuestionFact = factProvider.save(questionFormrunnerQuestionFact);
                        FormrunnerQuestionFacts.add(questionFormrunnerQuestionFact);
                    }
                }
            }
        }
    }

    @Test
    public void xmlFromFormrunnerQuestionFact() throws IOException, URISyntaxException {
        Assert.assertEquals(pivotViewProvider.xmlFormFacts(FormrunnerQuestionFacts), readFile("pivotviewer/FormrunnerQuestionFacts.xml"));
    }

    @AfterClass
    public void cleanUp() {
        for (FormrunnerQuestionFact fact : FormrunnerQuestionFacts) {
            factProvider.delete(fact);
        }
    }

    private String readFile(String file) throws IOException, InvalidJsonException, URISyntaxException {
        return new String(Files.readAllBytes(Paths.get(getClass().getClassLoader()
                .getResource(file).toURI())));
    }
}
