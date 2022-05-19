package com.biit.factmanager.test;

import com.biit.factmanager.core.providers.ChartProvider;
import com.biit.factmanager.core.providers.FactProvider;
import com.biit.factmanager.persistence.entities.FormRunnerFact;
import com.biit.factmanager.persistence.entities.values.FormRunnerValue;
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
    private FactProvider<FormRunnerFact> factProvider;

    @Autowired
    private ChartProvider<FormRunnerFact> chartProvider = new ChartProvider<FormRunnerFact>(factProvider);

    private final List<FormRunnerFact> formRunnerFacts = new ArrayList<>();

    @BeforeClass
    public void populate() {
        for (int patient = 0; patient <= 2; patient++) {
            for (int questions = 0; questions < 5; questions++) {
                FormRunnerFact formRunnerFact = new FormRunnerFact();
                FormRunnerValue formRunnerValue = new FormRunnerValue();

                formRunnerValue.setScore((double) (questions + patient));
                formRunnerValue.setQuestion("question" + questions);

                formRunnerFact.setTenantId("persona" + patient);
                formRunnerFact.setGroup("examination" + questions);
                formRunnerFact.setElementId("element" + patient);
                formRunnerFact.setEntity(formRunnerValue);

                formRunnerFacts.add(formRunnerFact);
            }
        }
    }

    @Test
    public void htmlFromFormrunnerFacts() throws IOException, URISyntaxException {
        Assert.assertEquals(readFile("charts/htmlFromFormrunnerFactsByTenants.html"),
                chartProvider.htmlFromFormrunnerFactsByQuestion(formRunnerFacts, "bar","0.7.20"));
    }

    private String readFile(String file) throws IOException, InvalidJsonException, URISyntaxException {
        return new String(Files.readAllBytes(Paths.get(getClass().getClassLoader()
                .getResource(file).toURI())));
    }
}
