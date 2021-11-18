package com.biit.factmanager.core.providers;

import com.biit.factmanager.logger.FactManagerLogger;
import com.biit.factmanager.persistence.entities.Fact;
import com.biit.factmanager.persistence.entities.FormRunnerValue;
import com.biit.factmanager.persistence.repositories.FactRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Service
public class PivotViewProvider<T> {
    private final FactRepository factRepository;

    @Autowired
    public PivotViewProvider(FactRepository factRepository) {
        this.factRepository = factRepository;
    }

    public String getCase(Long tenantId, String category, String elementId, LocalDateTime startDate, LocalDateTime endDate, Integer lastDays) {
        Collection<Fact<T>> facts = new ArrayList<>();
        if (startDate != null && endDate != null && lastDays == null) {
            getAllCombinations(tenantId, category, elementId, startDate, endDate);
        }
        if (startDate == null && endDate == null && lastDays != null) {
            final LocalDateTime localStartDate = LocalDateTime.now().minusDays(lastDays);
            final LocalDateTime localEndDate = LocalDateTime.now();
            getAllCombinations(tenantId, category, elementId, localStartDate, localEndDate);
        }
        if (startDate == null && endDate == null && lastDays == null) {
            getCombinationsWithoutDates(tenantId, category, elementId);
        }
        if (tenantId == null && "".equals(category) && "".equals(elementId) && startDate == null && endDate == null && lastDays == null) {
            facts = getAll();
        }
        try {
            getFormRunnerValueFromJson(facts.stream().findFirst().get());
            return xmlFromFormrunnerFact(facts);
        } catch (JsonProcessingException e) {
            return xmlFromFact(facts);
        } catch (IOException e) {
            FactManagerLogger.errorMessage(this.getClass(), e);
        }
        return xmlFromFact(facts);
    }

    public void getAllCombinations(Long tenantId, String category, String elementId, LocalDateTime startDate, LocalDateTime endDate) {
        Collection facts = new ArrayList<>();
        if (tenantId == null && "".equals(category) && "".equals(elementId)) {
            facts = factRepository.findByCreatedAtGreaterThanEqualAndCreatedAtLessThanEqual(startDate, endDate);
        }
        if (tenantId == null && "".equals(category) && !"".equals(elementId)) {
            facts = factRepository.findByElementIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThanEqual(elementId, startDate, endDate);
        }
        if (tenantId == null && !"".equals(category) && "".equals(elementId)) {
            facts = factRepository.findByCategoryAndCreatedAtGreaterThanEqualAndCreatedAtLessThanEqual(category, startDate, endDate);
        }
        if (tenantId == null && !"".equals(category) && !"".equals(elementId)) {
            facts = factRepository.findByElementIdAndCategoryAndCreatedAt(elementId, category, startDate, endDate);
        }
        if (tenantId != null && "".equals(category) && "".equals(elementId)) {
            facts = factRepository.findByTenantIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThanEqual(tenantId, startDate, endDate);
        }
        if (tenantId != null && "".equals(category) && !"".equals(elementId)) {
            facts = factRepository.findByTenantIdAndElementIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThanEqual(tenantId, elementId, startDate, endDate);
        }
        if (tenantId != null && !"".equals(category) && "".equals(elementId)) {
            facts = factRepository.findByTenantIdAndCategoryAndCreatedAtGreaterThanEqualAndCreatedAtLessThanEqual(tenantId, category, startDate, endDate);
        }
        if (tenantId != null && !"".equals(category) && !"".equals(elementId)) {
            facts = factRepository.findByTenantIdAndCategoryAndElementIdAndCreatedAt(tenantId, category, elementId, startDate, endDate);
        }
    }

    public void getCombinationsWithoutDates(Long tenantId, String category, String elementId) {
        Collection<T> facts = new ArrayList<>();
        if (tenantId == null && "".equals(category) && "".equals(elementId)) {
            facts = (Collection<T>) factRepository.findAll();
        }
        if (tenantId == null && "".equals(category) && !"".equals(elementId)) {
            facts = factRepository.findByElementId(elementId);
        }
        if (tenantId == null && !"".equals(category) && "".equals(elementId)) {
            facts = factRepository.findByCategory(category);
        }
        if (tenantId == null && !"".equals(category) && !"".equals(elementId)) {
            facts = factRepository.findByElementIdAndCategory(elementId, category);
        }
        if (tenantId != null && "".equals(category) && "".equals(elementId)) {
            facts = factRepository.findByTenantId(tenantId);
        }
        if (tenantId != null && "".equals(category) && !"".equals(elementId)) {
            facts = factRepository.findByTenantIdAndElementId(tenantId, elementId);
        }
        if (tenantId != null && !"".equals(category) && "".equals(elementId)) {
            facts = factRepository.findByTenantIdAndCategory(tenantId, category);
        }
        if (tenantId != null && !"".equals(category) && !"".equals(elementId)) {
            facts = factRepository.findByTenantIdAndCategoryAndElementId(tenantId, category, elementId);
        }
    }

    public Collection<Fact<T>> getAll() {
        return (Collection<Fact<T>>) factRepository.findAll();
    }

    public Collection<Fact<T>> getBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        return factRepository.findByCreatedAtGreaterThanEqualAndCreatedAtLessThanEqual(
                startDate, endDate);
    }

    public Collection<Fact<T>> getAfterDate(LocalDateTime date) {
        return factRepository.findByCreatedAtGreaterThan(date);
    }

    public Collection<Fact<T>> getBeforeDate(LocalDateTime date) {
        return factRepository.findByCreatedAtLessThan(date);
    }

    public Collection<Fact<T>> getLastXDays(Integer days) {
        final LocalDateTime dateBefore = LocalDateTime.now().minusDays(days);
        final LocalDateTime dateToday = LocalDateTime.now();
        return getBetweenDates(dateBefore, dateToday);
    }

    public String xmlFromFact(Collection<Fact<T>> facts) {
        final StringBuilder xml = new StringBuilder();
        final Set<String> categories = new HashSet<>();
        final Set<Long> tenantIds = new HashSet<>();
        for (final Fact<T> fact : facts) {
            categories.add(fact.getCategory());
            tenantIds.add(fact.getTenantId());
        }
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<Collection xmlns=\"http://schemas.microsoft.com/collection/metadata/2009\" SchemaVersion=\"1.0\" Name=\"FMS\">\n");
        xml.append("\t<FacetCategories>\n");
        for (final String category : categories) {
            xml.append("\t\t<FacetCategory Name=\"").append(category).append("\" Type=\"Number\" />\n");
        }
        xml.append("\t</FacetCategories>\n");
        tenantIds.forEach(tenantId -> {
            xml.append("\n\t\t<Item Id=\"").append(tenantId).append("\" Img=\"#2")
                    .append("\n#4\" Href=\"/usmo\" Name=\"").append(tenantId).append("\">").
                    append("\n\t\t\t<Facets>\n");
            facts.stream().filter(fact -> fact.getTenantId() == tenantId).forEach(fact -> {
                xml.append("\t\t\t\t<Facet Name=\"").append(fact.getCategory()).append("\">\n");
                xml.append("\t\t\t\t\t<Number Value=\"").append(fact.getValue()).append("\"/>\n");
                xml.append("\t\t\t\t</Facet>\n");
            });
            xml.append("\t\t</Facets>\n\t\t</Item>\n");
        });
        xml.append("\n\t</Items>");
        xml.append("\n</Collection>");
        return xml.toString();
    }

    public String xmlFromFormrunnerFact(Collection<Fact<T>> facts) throws IOException {
        final StringBuilder xml = new StringBuilder();
        final Set<String> categories = new HashSet<>();
        final Set<Long> tenantIds = new HashSet<>();
        for (final Fact<T> fact : facts) {
            categories.add(fact.getCategory());
            tenantIds.add(fact.getTenantId());
        }
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<Collection xmlns=\"http://schemas.microsoft.com/collection/metadata/2009\" SchemaVersion=\"1.0\" Name=\"FMS\">\n");
        xml.append("\t<FacetCategories>\n");
        for (final String category : categories) {
            xml.append("\t\t<FacetCategory Name=\"").append(category).append("\" Type=\"Number\" />\n");
        }
        xml.append("\t</FacetCategories>\n");
        xml.append("\t<Items ImgBase=\"").append("./dz_haagse_passage/haagse_passage.dzc").append("\">\n");
        tenantIds.forEach(tenantId -> {
            xml.append("\n\t\t<Item Id=\"").append(tenantId).append("\" Img=\"#2")
                    .append("\n#4\" Href=\"/usmo\" Name=\"").append(tenantId).append("\">").
                    append("\n\t\t\t<Facets>\n");
            facts.stream().filter(fact -> fact.getTenantId() == tenantId).forEach(fact -> {
                FormRunnerValue formRunnerValue = new FormRunnerValue();
                try {
                    formRunnerValue = getFormRunnerValueFromJson(fact);
                } catch (JsonProcessingException e) {
                    FactManagerLogger.errorMessage(this.getClass(), e);
                }
                xml.append("\t\t\t\t<Facet Name=\"").append(formRunnerValue.getCategory()).append("\">\n");
                xml.append("\t\t\t\t\t<Number Value=\"").append(formRunnerValue.getScore()).append("\"/>\n");
                xml.append("\t\t\t\t</Facet>\n");
            });
            xml.append("\t\t</Facets>\n\t\t</Item>\n");
        });
        xml.append("\n\t</Items>");
        xml.append("\n</Collection>");
        return xml.toString();
    }

    private FormRunnerValue getFormRunnerValueFromJson(Fact fact) throws JsonProcessingException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final FormRunnerValue formRunnerValue = objectMapper.readValue(fact.getValue(), FormRunnerValue.class);
        return formRunnerValue;
    }
}