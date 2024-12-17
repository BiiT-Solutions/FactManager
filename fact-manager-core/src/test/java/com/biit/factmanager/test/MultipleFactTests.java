package com.biit.factmanager.test;

import com.biit.factmanager.core.providers.FactProvider;
import com.biit.factmanager.persistence.entities.FormrunnerQuestionFact;
import com.biit.factmanager.persistence.entities.FormrunnerVariableFact;
import com.biit.factmanager.persistence.entities.LogFact;
import com.biit.factmanager.persistence.entities.values.FormrunnerQuestionValue;
import com.biit.factmanager.persistence.entities.values.FormrunnerVariableValue;
import com.biit.factmanager.persistence.repositories.FactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

@SpringBootTest
@Test(groups = {"multipleFacts"})
public class MultipleFactTests extends AbstractTestNGSpringContextTests {

    @Autowired
    private FactRepository<?> overlordFactRepository;

    @Autowired
    private FactProvider<?> overlordFactProvider;

    @Autowired
    private FactProvider<FormrunnerVariableFact> formrunnerVariableFactProvider;

    @Autowired
    private FactProvider<LogFact> stringFactFactProvider;

    @Autowired
    private FactProvider<FormrunnerQuestionFact> formrunnerQuestionFactFactProvider;

    @BeforeClass
    public void setUp() {
        //Set form scores (image index).
        FormrunnerVariableFact formrunnerVariableFact = new FormrunnerVariableFact();
        formrunnerVariableFact.setGroup("GROUP");
        formrunnerVariableFact.setElement("examinationId");
        formrunnerVariableFact.setTenant("p");

        //Forms scores has no question field filled up.
        FormrunnerVariableValue formrunnerVariableValue = new FormrunnerVariableValue();
        formrunnerVariableValue.setXpath("formXpath");
        formrunnerVariableValue.setItemName("Patient");
        formrunnerVariableFact.setEntity(formrunnerVariableValue);
        formrunnerVariableFactProvider.save(formrunnerVariableFact);

        LogFact logFact = new LogFact();
        logFact.setString("Content of fact");
        logFact.setGroup("GROUP");
        stringFactFactProvider.save(logFact);

        FormrunnerQuestionFact formrunnerQuestionFact = new FormrunnerQuestionFact();
        FormrunnerQuestionValue formRunnerValue = new FormrunnerQuestionValue();
        formRunnerValue.setAnswer(" ");
        formRunnerValue.setXpath("question");
        formrunnerQuestionFact.setTenant("tenantId");
        formrunnerQuestionFact.setSession("sessionId");
        formrunnerQuestionFact.setSubject("SUBJECT");
        formrunnerQuestionFact.setGroup("GROUP");
        formrunnerQuestionFact.setElement("ELEMENT_ID");
        formrunnerQuestionFact.setFactType("FACTTYPE");
        formrunnerQuestionFact.setOrganization("ORGANIZATION_ID");
        formrunnerQuestionFact.setEntity(formRunnerValue);
        formrunnerQuestionFactFactProvider.save(formrunnerQuestionFact);
    }

    @Test
    public void countTypesFromRepository() {
        Assert.assertEquals(overlordFactRepository.count(), 3);
        List<?> facts = overlordFactRepository.findAll();
        Assert.assertEquals(facts.size(), 3);
    }

    @Test
    public void countTypesFromProvider() {
        Assert.assertEquals(overlordFactProvider.count(), 3);
        List<?> facts = overlordFactProvider.getAll();
        Assert.assertEquals(facts.size(), 3);
    }

    @Test
    public void findTypesFromProvider() {
        List<?> facts = overlordFactProvider.findBy(null, null, null, null, null, null, null, "GROUP", null, null, null, null, null, null, null);
        Assert.assertEquals(facts.size(), 3);
    }
}
