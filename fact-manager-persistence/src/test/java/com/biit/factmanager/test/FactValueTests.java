package com.biit.factmanager.test;

import com.biit.factmanager.persistence.entities.FormrunnerQuestionFact;
import com.biit.factmanager.persistence.entities.values.FormrunnerQuestionValue;
import com.biit.factmanager.persistence.repositories.FactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@SpringBootTest
@Test(groups = "factRepository")
@Rollback(false)
public class FactValueTests extends AbstractTransactionalTestNGSpringContextTests {

    @Autowired
    private FactRepository<FormrunnerQuestionFact> formrunnerQuestionFactRepository;

    @BeforeClass
    public void populate() {
        formrunnerQuestionFactRepository.deleteAll();
        for (int i = 0; i < 10; i++) {
            FormrunnerQuestionValue formrunnerQuestionValue = new FormrunnerQuestionValue();
            formrunnerQuestionValue.setAnswer("answer" + i);
            formrunnerQuestionValue.setCompanyId((long) i);
            formrunnerQuestionValue.setProfessionalId((long) i);

            FormrunnerQuestionFact formrunnerQuestionFact = new FormrunnerQuestionFact();
            formrunnerQuestionFact.setTag("tag" + i);
            formrunnerQuestionFact.setGroup("group" + i);
            formrunnerQuestionFact.setElement("elementId" + i);
            formrunnerQuestionFact.setTenant("tenantId" + i);
            formrunnerQuestionFact.setOrganization("organizationId" + i);
            formrunnerQuestionFact.setEntity(formrunnerQuestionValue);
            Long id = formrunnerQuestionFactRepository.save(formrunnerQuestionFact).getId();
            FormrunnerQuestionFact formrunnerQuestionFactDB = formrunnerQuestionFactRepository.findById(id).orElse(null);
            Assert.assertNotNull(formrunnerQuestionFactDB);
        }
    }

    @Test
    public void valueNotNull() {
        formrunnerQuestionFactRepository.findAll().forEach(formrunnerQuestionFact -> {
            Assert.assertNotNull(formrunnerQuestionFact.getValue());
            Assert.assertNotNull(formrunnerQuestionFact.getEntity().getQuestion());
        });
    }

    @AfterClass
    public void tearDown() {
        formrunnerQuestionFactRepository.deleteAll();
    }
}
