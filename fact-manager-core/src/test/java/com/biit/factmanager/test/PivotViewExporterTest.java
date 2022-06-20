package com.biit.factmanager.test;

import com.biit.factmanager.core.providers.FactProvider;
import com.biit.factmanager.core.providers.PivotViewProvider;
import com.biit.factmanager.persistence.entities.FormrunnerVariableFact;
import com.biit.factmanager.persistence.entities.values.FormrunnerVariableValue;
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
    private FactProvider<FormrunnerVariableFact<Double>> formrunnerVariableFactProvider;

    @Autowired
    private PivotViewProvider<FormrunnerVariableFact<Double>> pivotViewProvider;

    private final List<FormrunnerVariableFact<Double>> formrunnerVariablesFacts = new ArrayList<>();

    @BeforeClass
    public void populate() {
        //5 Patients
        for (int patient = 0; patient < 5; patient++) {
            // 2 Examinations
            for (int examination = 1; examination < 3; examination++) {
                String formXpath = "/form";
                //Set form scores (image index).
                FormrunnerVariableFact<Double> formrunnerVariableFact = new FormrunnerVariableFact<>();
                formrunnerVariableFact.setGroup("examination" + examination);
                formrunnerVariableFact.setElementId("examinationId" + examination);
                formrunnerVariableFact.setTenantId("p" + patient);

                //Forms scores has not question field filled up.
                FormrunnerVariableValue<Double> formrunnerVariableValue = new FormrunnerVariableValue<>();
                //formrunnerVariableValue.setValue((double) examination);
                formrunnerVariableValue.setXpath(formXpath);
                formrunnerVariableValue.setItemName("Patient" + patient);
                formrunnerVariableFact.setEntity(formrunnerVariableValue);
                formrunnerVariableFact = formrunnerVariableFactProvider.save(formrunnerVariableFact);
                formrunnerVariablesFacts.add(formrunnerVariableFact);

                // Each examination 2 categories
                for (int category = 1; category < 3; category++) {
                    String categoryXpath = formXpath + "/category" + category;
                    //Set category.
                    FormrunnerVariableFact<Double> categoryFormrunnerVariableFact = new FormrunnerVariableFact<>();
                    categoryFormrunnerVariableFact.setGroup("examination" + examination);
                    categoryFormrunnerVariableFact.setElementId("examinationId" + examination);
                    categoryFormrunnerVariableFact.setTenantId("p" + patient);

                    //Categories scores has no question field filled up.
                    FormrunnerVariableValue<Double> categoryFormrunnerVariableValue = new FormrunnerVariableValue<>();
                    categoryFormrunnerVariableValue.setXpath(categoryXpath);
                    categoryFormrunnerVariableValue.setItemName("Patient" + patient);
                    //categoryFormrunnerVariableValue.setValue((double) category);
                    categoryFormrunnerVariableFact.setEntity(categoryFormrunnerVariableValue);

                    categoryFormrunnerVariableFact = formrunnerVariableFactProvider.save(categoryFormrunnerVariableFact);
                    formrunnerVariablesFacts.add(categoryFormrunnerVariableFact);
                    // Each Examination with 10 questions
                    for (int question = 0; question < 10; question++) {
                        String questionXpath = categoryXpath + String.format("/Question_%s_%s_%s", examination, category, question);
                        FormrunnerVariableFact<Double> questionFormrunnerVariableFact = new FormrunnerVariableFact<>();
                        questionFormrunnerVariableFact.setGroup("examination" + examination);
                        questionFormrunnerVariableFact.setElementId("examinationId" + examination);
                        questionFormrunnerVariableFact.setTag("tag");
                        questionFormrunnerVariableFact.setTenantId("p" + patient);

                        FormrunnerVariableValue<Double> questionFormrunnerQuestionValue = new FormrunnerVariableValue<>();
                        questionFormrunnerQuestionValue.setItemName(FormrunnerVariableValue.SCORE_VALUE);
                        questionFormrunnerQuestionValue.setValue(Double.parseDouble("0." + question));

                        questionFormrunnerQuestionValue.setXpath(questionXpath);
                        questionFormrunnerQuestionValue.setItemName("Patient" + patient);
                        questionFormrunnerVariableFact.setEntity(questionFormrunnerQuestionValue);

                        questionFormrunnerVariableFact = formrunnerVariableFactProvider.save(questionFormrunnerVariableFact);
                        formrunnerVariablesFacts.add(questionFormrunnerVariableFact);
                    }
                }
            }
        }
    }

    @Test
    public void xmlFromFormrunnerQuestionFact() throws IOException, URISyntaxException {
        Assert.assertEquals(pivotViewProvider.xmlFormFacts(formrunnerVariablesFacts), readFile("pivotviewer/FormrunnerQuestionFacts.xml"));
    }

    @AfterClass
    public void cleanUp() {
        for (FormrunnerVariableFact<Double> fact : formrunnerVariablesFacts) {
            formrunnerVariableFactProvider.delete(fact);
        }
    }

    private String readFile(String file) throws IOException, InvalidJsonException, URISyntaxException {
        return new String(Files.readAllBytes(Paths.get(getClass().getClassLoader()
                .getResource(file).toURI())));
    }
}
