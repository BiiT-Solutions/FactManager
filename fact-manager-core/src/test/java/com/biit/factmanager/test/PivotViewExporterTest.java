package com.biit.factmanager.test;

import com.biit.factmanager.persistence.entities.Fact;
import com.biit.factmanager.persistence.entities.FormRunnerValue;
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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SpringBootTest
@Rollback(value = false)
@Test(groups = {"pivotViewExporter"})
public class PivotViewExporterTest extends AbstractTestNGSpringContextTests {

    private static final String FACT_EXAMINATION_NAME = "examination_name";
    private static final Long FACT_ID = 1L;

    @Autowired
    private FormRunnerFactRepository formrunnerFactRepository;

    private final List<FormRunnerFact> formRunnerFacts = new ArrayList<>();

    @BeforeClass
    public void populate() {
        for (int i = 0; i < 10; i++) {
            FormRunnerFact formrunnerFact = new FormRunnerFact();
            formrunnerFact.setCategory("category" + i);
            formrunnerFact.setElementId("elementId" + i);
            formrunnerFact.setValue("0." + i);
            formRunnerFacts.add(formrunnerFact);
            formrunnerFactRepository.save(formrunnerFact);
        }
    }

    @Test
    public void addFact() {
        long existingValues = formrunnerFactRepository.count();
        FormRunnerFact formrunnerFact = new FormRunnerFact();
        formRunnerFacts.add(formrunnerFact);
        formrunnerFactRepository.save(formrunnerFact);
        Assert.assertEquals(formrunnerFactRepository.count(), existingValues + 1);
    }

    @Test(dependsOnMethods = "addFact")
    public void xmlFromFormRunnerFact() {
        final StringBuilder xml = new StringBuilder();
        final Set<String> categories = new HashSet<>();
        final Set<Long> tenantIds = new HashSet<>();
        for (final FormRunnerFact fact : formRunnerFacts) {
            categories.add(fact.getCategory());
            tenantIds.add(fact.getTenantId());
        }
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<Collection xmlns=\"http://schemas.microsoft.com/collection/metadata/2009\" SchemaVersion=\"1.0\" Name=\"FMS\">\n");
        xml.append("   <FacetCategories>\n");
        for (final String category : categories) {
            xml.append("      <FacetCategory Name=\"").append(category).append("\" Type=\"Number\" />\n");
        }
        xml.append("   </FacetCategories>\n");
        xml.append("  <Items ImgBase=\"").append("./dz_haagse_passage/haagse_passage.dzc").append("\">\n");
        tenantIds.forEach(tenantId -> {
            xml.append("\n\n\t     <Item Id=\"").append(tenantId).append("\" Img=\"#2")
                    .append("\n#4\" Href=\"/usmo\" Name=\"").append(tenantId).append("\">").
                    append("\n\t\t <Facets>\n");
            formRunnerFacts.stream().filter(fact -> fact.getTenantId() == tenantId).forEach(fact -> {
                FormRunnerValue formRunnerValue = new FormRunnerValue();
                try {
                    formRunnerValue = getFormRunnerValueFromJson(fact);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                xml.append("\t\t    <Facet Name=\"").append(formRunnerValue.getCategory()).append("\">\n");
                xml.append("\t\t       <Number Value=\"").append(formRunnerValue.getScore()).append("\"/>\n");
                xml.append("\t\t    </Facet>\n");
            });
            xml.append("\t\t</Facets>\n\t      </Item>\n");
        });
        xml.append("\n\n   </Items>");
        xml.append("\n</Collection>");
        System.out.print(xml);
    }

    public void xmlFromFact() {
        final StringBuilder xml = new StringBuilder();
        final Set<String> categories = new HashSet<>();
        final Set<Long> tenantIds = new HashSet<>();
        for (final FormRunnerFact fact : formRunnerFacts) {
            categories.add(fact.getCategory());
            tenantIds.add(fact.getTenantId());
        }
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<Collection xmlns=\"http://schemas.microsoft.com/collection/metadata/2009\" SchemaVersion=\"1.0\" Name=\"FMS\">\n");
        xml.append("   <FacetCategories>\n");
        for (final String category : categories) {
            xml.append("      <FacetCategory Name=\"").append(category).append("\" Type=\"Number\" />\n");
        }
        xml.append("   </FacetCategories>\n");
        tenantIds.forEach(tenantId -> {
            xml.append("\n\n\t     <Item Id=\"").append(tenantId).append("\" Img=\"#2")
                    .append("\n#4\" Href=\"/usmo\" Name=\"").append(tenantId).append("\">").
                    append("\n\t\t <Facets>\n");
            formRunnerFacts.stream().filter(fact -> fact.getTenantId() == tenantId).forEach(fact -> {
                xml.append("\t\t    <Facet Name=\"").append(fact.getCategory()).append("\">\n");
                xml.append("\t\t       <Number Value=\"").append(fact.getValue()).append("\"/>\n");
                xml.append("\t\t    </Facet>\n");
            });
            xml.append("\t\t</Facets>\n\t      </Item>\n");
        });
        xml.append("\n\n   </Items>");
        xml.append("\n</Collection>");
        System.out.print(xml);
    }

    private FormRunnerValue getFormRunnerValueFromJson(Fact fact) throws JsonProcessingException, JSONException {
        ObjectMapper objectMapper = new ObjectMapper();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("score", fact.getValue());
        jsonObject.put("category", fact.getCategory());
        FormRunnerValue formRunnerValue = objectMapper.readValue(jsonObject.toString(), FormRunnerValue.class);
        return formRunnerValue;
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
