package com.biit.factmanager.test;

import com.biit.factmanager.core.providers.ChartProvider;
import com.biit.factmanager.core.providers.FactProvider;
import com.biit.factmanager.persistence.entities.FormrunnerQuestionFact;
import com.biit.factmanager.persistence.entities.values.FormrunnerQuestionValue;
import com.jayway.jsonpath.InvalidJsonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@Test(groups = {"facts"})
public class ChartProviderTest {

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
        Assert.assertEquals(readFile("charts/htmlFromformrunnerQuestionFactsByTenants.html"),
                chartProvider.htmlFromformrunnerQuestionFactsByQuestion(formrunnerQuestionFacts, "bar","0.7.20"));
    }

    private String readFile(String file) throws IOException, InvalidJsonException, URISyntaxException {
        return new String(Files.readAllBytes(Paths.get(getClass().getClassLoader()
                .getResource(file).toURI())));
    }
}
