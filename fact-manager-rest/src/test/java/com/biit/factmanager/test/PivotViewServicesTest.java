package com.biit.factmanager.test;

import com.biit.factmanager.persistence.entities.FormrunnerFact;
import com.biit.factmanager.persistence.enums.Level;
import com.biit.factmanager.rest.api.FactServices;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

@SpringBootTest
@Rollback(value = false)
@Test(groups = {"pivotViewServices"})
public class PivotViewServicesTest extends AbstractTestNGSpringContextTests {

    private static final String FACT_EXAMINATION_NAME = "examination_name";
    private static final Long FACT_ID = 1L;

    @Autowired
    private FactServices factServices;

    private List<FormrunnerFact> formrunnerFacts = new ArrayList<>();

    @BeforeClass
    public void populate() {
        for (int i = 0; i < 10; i++) {
            FormrunnerFact formrunnerFact = new FormrunnerFact();
            formrunnerFact.setExaminationName("Antopometrie"+i);
            formrunnerFact.setPatientId(i);
            formrunnerFact.setScore(Math.random() * 4);
            formrunnerFacts.add(formrunnerFact);
            System.out.println("fact n "+i);
        }
        factServices.addFactList(formrunnerFacts,null);
    }

    @Test
    public void addFact() {
        FormrunnerFact formrunnerFact = new FormrunnerFact();
        formrunnerFacts.add(formrunnerFact);
        factServices.addFactList(formrunnerFacts,null);
        Assert.assertEquals(factServices.getFacts(FACT_ID, FACT_EXAMINATION_NAME, null, null).size(),12);
    }

    @Test(dependsOnMethods = "xmlFromFact")
    public void removeFact() {
        for (FormrunnerFact fact : formrunnerFacts) {
            factServices.deleteFact(fact, null);
        }
        Assert.assertEquals(factServices.getFacts(FACT_ID, FACT_EXAMINATION_NAME, Level.COMPANY, null).size(), 1);
    }

    @Test(dependsOnMethods = "addFact")
    public void xmlFromFact() throws IOException {
        populate();
        FileWriter fichero = new FileWriter("/home/simon/Documentos/prueba.xml");
        PrintWriter pw = new PrintWriter(fichero);
        List<String> categories = new ArrayList<String>();
        List<Long> patientsIds = new ArrayList<Long>();
        for (int i = 1; i < formrunnerFacts.size(); i++) {
            categories.add(formrunnerFacts.get(i).getExaminationName());
            patientsIds.add(formrunnerFacts.get(i).getPatientId());
        }
        categories = categories.stream().distinct().collect(Collectors.toList());
        patientsIds = patientsIds.stream().distinct().collect(Collectors.toList());
        pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        pw.println("<Collection xmlns=\"http://schemas.microsoft.com/collection/metadata/2009\" SchemaVersion=\"1.0\" Name=\"FMS\">");
        pw.println("<Categories>");
        for (String category : categories) {
            pw.println("<FacetCategory Name=\"" + category + "\" Type=\"Number\" />");
        }
        pw.println("</Categories>");
            for (int i = 0; i < patientsIds.size(); i++) {
                pw.println("<Item Id=\"7ca95d7f-2852-42c7-b09d-529bef36e093\" Img=\"#1\"\n\n" +
                           "Href=\"/usmo\" Name=\"" + patientsIds.get(i) + "\">" +
                           "\n \t<Facets>");
                for (int j = 1; j < formrunnerFacts.size(); j++) {
                    if (patientsIds.get(i) == formrunnerFacts.get(j).getPatientId()) {
                        pw.println("\t\t<Facet Name=\"" + formrunnerFacts.get(j).getExaminationName() + "\">");
                        pw.println("\t\t\t<Number Value=\"" + formrunnerFacts.get(j).getScore() + "\"/>");
                        pw.println("</Facet>");
                    }
                }
                pw.println(" </Facets>\n</Item>");
            }
        pw.println("</Collection>");
        fichero.close();
    }

    @AfterClass
    public void cleanUp() {
        for (FormrunnerFact fact: formrunnerFacts) {
            factServices.deleteFact(fact,null);
        }
    }
}
