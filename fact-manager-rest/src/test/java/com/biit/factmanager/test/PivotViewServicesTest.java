package com.biit.factmanager.test;

import com.biit.factmanager.persistence.entities.FormrunnerFact;
import com.biit.factmanager.persistence.repositories.FormrunnerFactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.StreamSupport;

@SpringBootTest
@Rollback(value = false)
@Test(groups = {"pivotViewServices"})
public class PivotViewServicesTest extends AbstractTestNGSpringContextTests {

    private static final String FACT_EXAMINATION_NAME = "examination_name";
    private static final Long FACT_ID = 1L;

    @Autowired
    private FormrunnerFactRepository formrunnerFactRepository;

    private List<FormrunnerFact> formrunnerFacts = new ArrayList<>();

    @BeforeClass
    public void populate() {
        for (int i = 0; i < 10; i++) {
            FormrunnerFact formrunnerFact = new FormrunnerFact();
            formrunnerFact.setCategory("categoria"+i);
            formrunnerFact.setElementId("elementid"+i);
            formrunnerFacts.add(formrunnerFact);
            System.out.println("fact n "+i);
            formrunnerFactRepository.save(formrunnerFact);
        }
    }

    @Test
    public void addFact() {
        FormrunnerFact formrunnerFact = new FormrunnerFact();
        formrunnerFacts.add(formrunnerFact);
        formrunnerFactRepository.save(formrunnerFact);
        Assert.assertEquals(StreamSupport.stream(formrunnerFactRepository.findAll().spliterator(),false).count(),12);
    }

    @Test(dependsOnMethods = "xmlFromFact")
    public void removeFact() {
        for (FormrunnerFact fact : formrunnerFacts) {
            formrunnerFactRepository.delete(fact);
        }
        Assert.assertEquals(StreamSupport.stream(formrunnerFactRepository.findAll().spliterator(),false).count(), 0);
    }

    @Test(dependsOnMethods = "addFact")
    public void xmlFromFact() throws IOException {
        populate();
        final StringBuilder xml = new StringBuilder();
        final Set<String> categories = new HashSet<>();
        final Set<Long> tenantIds = new HashSet<>();
        for (final FormrunnerFact fact : formrunnerFacts) {
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
            formrunnerFacts.stream().filter(fact -> fact.getTenantId() == tenantId).forEach(fact -> {
                xml.append("\t\t    <Facet Name=\"").append(fact.getCategory()).append("\">\n");
                //xml.append("\t\t       <Number Value=\"").append(fact.getValue()).append("\"/>\n");
                xml.append("\t\t    </Facet>\n");
            });
            xml.append("\t\t</Facets>\n\t      </Item>\n");
        });
        xml.append("\n\n   </Items>");
        xml.append("\n</Collection>");
        System.out.print(xml);
    }

    @AfterClass
    public void cleanUp() {
        for (FormrunnerFact fact: formrunnerFacts) {
            formrunnerFactRepository.delete(fact);
        }
    }
}
