package com.biit.factmanager.core.providers;

import com.biit.factmanager.persistence.entities.FormrunnerFact;
import com.biit.factmanager.persistence.repositories.FormrunnerFactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class PivotViewProvider {
    private final FormrunnerFactRepository formrunnerFactRepository;

    @Autowired
    public PivotViewProvider(FormrunnerFactRepository formrunnerFactRepository) {
        this.formrunnerFactRepository = formrunnerFactRepository;
    }

    public StringBuilder getCase(Long tenantId, String category, String elementId, LocalDateTime startDate, LocalDateTime endDate, Integer lastDays) {
        Collection<FormrunnerFact> formrunnerFacts = new ArrayList<>();
        if (startDate != null && endDate != null && lastDays == null) {
            getAllCombinations(tenantId, category, elementId, startDate, endDate);
        }
        if (startDate == null && endDate == null && lastDays != null) {
            final LocalDateTime localStartDate = LocalDateTime.now().minusDays(lastDays);
            final LocalDateTime localEndDate = LocalDateTime.now();
            getAllCombinations(tenantId, category, elementId, localStartDate, localEndDate);
        }
        if (startDate == null && endDate == null && lastDays == null) {
            final LocalDateTime localStartDate = LocalDateTime.now().minusYears(100);
            final LocalDateTime localEndDate = LocalDateTime.now().plusYears(100);
            getAllCombinations(tenantId, category, elementId, localStartDate, localEndDate);
        }
        if (tenantId == null && "".equals(category) && "".equals(elementId) && startDate == null && endDate == null && lastDays == null) {
            formrunnerFacts = getAll();
        }
        return factToXml(formrunnerFacts);
    }

    public void getAllCombinations(Long tenantId, String category, String elementId, LocalDateTime startDate, LocalDateTime endDate) {
        Collection<FormrunnerFact> formrunnerFacts = new ArrayList<>();
        if (tenantId == null && "".equals(category) && "".equals(elementId)) {
            formrunnerFacts = getBetweenDates(startDate, endDate);
        }
        if (tenantId == null && "".equals(category) && !"".equals(elementId)) {
            formrunnerFacts = StreamSupport.stream(formrunnerFactRepository.findByElementIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThanEqual
                    (elementId, startDate, endDate).spliterator(), false).collect(Collectors.toSet());
        }
        if (tenantId == null && !"".equals(category) && "".equals(elementId)) {
            formrunnerFacts = StreamSupport.stream(formrunnerFactRepository.findByCategoryAndCreatedAtGreaterThanEqualAndCreatedAtLessThanEqual
                    (category, startDate, endDate).spliterator(), false).collect(Collectors.toSet());
        }
        if (tenantId == null && !"".equals(category) && !"".equals(elementId)) {
            formrunnerFacts = StreamSupport.stream(formrunnerFactRepository.findByElementIdAndCategoryAndCreatedAt
                    (elementId, category, startDate, endDate).spliterator(), false).collect(Collectors.toSet());
        }
        if (tenantId != null && "".equals(category) && "".equals(elementId)) {
            formrunnerFacts = StreamSupport.stream(formrunnerFactRepository.findByTenantIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThanEqual
                    (tenantId, startDate, endDate).spliterator(), false).collect(Collectors.toSet());
        }
        if (tenantId != null && "".equals(category) && !"".equals(elementId)) {
            formrunnerFacts = StreamSupport.stream(formrunnerFactRepository.findByTenantIdAndElementIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThanEqual
                    (tenantId, elementId, startDate, endDate).spliterator(), false).collect(Collectors.toSet());
        }
        if (tenantId != null && !"".equals(category) && "".equals(elementId)) {
            formrunnerFacts = StreamSupport.stream(formrunnerFactRepository.findByTenantIdAndCategoryAndCreatedAtGreaterThanEqualAndCreatedAtLessThanEqual
                    (tenantId, category, startDate, endDate).spliterator(), false).collect(Collectors.toSet());
        }
        if (tenantId != null && !"".equals(category) && !"".equals(elementId)) {
            formrunnerFacts = StreamSupport.stream(formrunnerFactRepository.findByTenantIdAndCategoryAndElementIdAndCreatedAt
                    (tenantId, category, elementId, startDate, endDate).spliterator(), false).collect(Collectors.toSet());
        }
    }

    public Collection<FormrunnerFact> getAll() {
        return StreamSupport.stream(formrunnerFactRepository.findAll().spliterator(), false).collect(Collectors.toSet());
    }

    public Collection<FormrunnerFact> getBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        return StreamSupport.stream(formrunnerFactRepository.findByCreatedAtGreaterThanEqualAndCreatedAtLessThanEqual(
                startDate, endDate).spliterator(), false).collect(Collectors.toSet());
    }

    public Collection<FormrunnerFact> getAfterDate(LocalDateTime date) {
        return StreamSupport.stream(formrunnerFactRepository.findByCreatedAtGreaterThan(date).spliterator(), false)
                .collect(Collectors.toSet());
    }

    public Collection<FormrunnerFact> getBeforeDate(LocalDateTime date) {
        return StreamSupport.stream(formrunnerFactRepository.findByCreatedAtLessThan(date).spliterator(), false)
                .collect(Collectors.toSet());
    }

    public Collection<FormrunnerFact> getLastXDays(Integer days) {
        final LocalDateTime dateBefore = LocalDateTime.now().minusDays(days);
        final LocalDateTime dateToday = LocalDateTime.now();
        return getBetweenDates(dateBefore, dateToday);
    }

    public StringBuilder factToXml(Collection<FormrunnerFact> facts) {
        final StringBuilder xml = new StringBuilder();
        final Set<String> categories = new HashSet<>();
        final Set<Long> tenantIds = new HashSet<>();
        for (final FormrunnerFact fact : facts) {
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
            facts.stream().filter(fact -> fact.getTenantId() == tenantId).forEach(fact -> {
                xml.append("\t\t    <Facet Name=\"").append(fact.getCategory()).append("\">\n");
                xml.append("\t\t       <Number Value=\"").append(fact.getValue()).append("\"/>\n");
                xml.append("\t\t    </Facet>\n");
            });
            xml.append("\t\t</Facets>\n\t      </Item>\n");
        });
        xml.append("\n\n   </Items>");
        xml.append("\n</Collection>");
        return xml;
    }
}