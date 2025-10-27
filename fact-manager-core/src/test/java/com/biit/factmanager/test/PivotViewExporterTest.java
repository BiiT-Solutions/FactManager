package com.biit.factmanager.test;

/*-
 * #%L
 * FactManager (core)
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
    private FactProvider<FormrunnerVariableFact> formrunnerVariableFactProvider;

    @Autowired
    private PivotViewProvider<FormrunnerVariableFact> pivotViewProvider;

    private final List<FormrunnerVariableFact> formrunnerVariablesFacts = new ArrayList<>();

    @BeforeClass
    public void populate() {
        //5 Patients
        for (int patient = 0; patient < 5; patient++) {
            // 2 Examinations
            for (int examination = 1; examination < 3; examination++) {
                String formXpath = "/form";
                //Set form scores (image index).
                FormrunnerVariableFact formrunnerVariableFact = new FormrunnerVariableFact();
                formrunnerVariableFact.setGroup("examination" + examination);
                formrunnerVariableFact.setElement("examinationId" + examination);
                formrunnerVariableFact.setTenant("p" + patient);

                //Forms scores has not question field filled up.
                FormrunnerVariableValue formrunnerVariableValue = new FormrunnerVariableValue();
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
                    FormrunnerVariableFact categoryFormrunnerVariableFact = new FormrunnerVariableFact();
                    categoryFormrunnerVariableFact.setGroup("examination" + examination);
                    categoryFormrunnerVariableFact.setElement("examinationId" + examination);
                    categoryFormrunnerVariableFact.setTenant("p" + patient);

                    //Categories scores has no question field filled up.
                    FormrunnerVariableValue categoryFormrunnerVariableValue = new FormrunnerVariableValue();
                    categoryFormrunnerVariableValue.setXpath(categoryXpath);
                    categoryFormrunnerVariableValue.setItemName("Patient" + patient);
                    //categoryFormrunnerVariableValue.setValue((double) category);
                    categoryFormrunnerVariableFact.setEntity(categoryFormrunnerVariableValue);

                    categoryFormrunnerVariableFact = formrunnerVariableFactProvider.save(categoryFormrunnerVariableFact);
                    formrunnerVariablesFacts.add(categoryFormrunnerVariableFact);
                    // Each Examination with 10 questions
                    for (int question = 0; question < 10; question++) {
                        String questionXpath = categoryXpath + String.format("/Question_%s_%s_%s", examination, category, question);
                        FormrunnerVariableFact questionFormrunnerVariableFact = new FormrunnerVariableFact();
                        questionFormrunnerVariableFact.setGroup("examination" + examination);
                        questionFormrunnerVariableFact.setElement("examinationId" + examination);
                        questionFormrunnerVariableFact.setSubject("subject");
                        questionFormrunnerVariableFact.setTenant("p" + patient);

                        FormrunnerVariableValue questionFormrunnerQuestionValue = new FormrunnerVariableValue();
                        questionFormrunnerQuestionValue.setItemName(FormrunnerVariableValue.SCORE_VALUE);
                        questionFormrunnerQuestionValue.setValue("0." + question);

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
        for (FormrunnerVariableFact fact : formrunnerVariablesFacts) {
            formrunnerVariableFactProvider.delete(fact);
        }
    }

    private String readFile(String file) throws IOException, InvalidJsonException, URISyntaxException {
        return new String(Files.readAllBytes(Paths.get(getClass().getClassLoader()
                .getResource(file).toURI())));
    }
}
