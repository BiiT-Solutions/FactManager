package com.biit.factmanager.core.providers;

import com.biit.factmanager.core.providers.exceptions.FactNotFoundException;
import com.biit.factmanager.logger.FactManagerLogger;
import com.biit.factmanager.persistence.entities.Fact;
import com.biit.factmanager.persistence.entities.values.FormRunnerValue;
import com.biit.factmanager.persistence.repositories.FactRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class PivotViewProvider<E, T extends Fact<E>> {
    private final FactRepository<E, T> factRepository;

    @Autowired
    public PivotViewProvider(FactRepository<E, T> factRepository) {
        this.factRepository = factRepository;
    }

    public String getCase(Long tenantId, String tag, String category, String elementId, LocalDateTime startDate, LocalDateTime endDate, Integer lastDays) throws
            FactNotFoundException {
        Collection<T> facts;
        if (lastDays == null) {
            facts = getAll(tenantId, tag, category, elementId, startDate, endDate);
        } else {
            final LocalDateTime localStartDate = LocalDateTime.now().minusDays(lastDays);
            final LocalDateTime localEndDate = LocalDateTime.now();
            facts = getAll(tenantId, tag, category, elementId, localStartDate, localEndDate);
        }
        try {
            return xmlFromFormRunnerFact(facts);
        } catch (IOException e) {
            FactManagerLogger.errorMessage(this.getClass(), e);
        }
        return xmlFromFact(facts);
    }

    public Collection<T> getAll(Long tenantId, String tag, String category, String elementId, LocalDateTime startDate, LocalDateTime endDate) {
        return factRepository.findBy(tenantId, tag, category, elementId, startDate, endDate);
    }

    public String xmlFromFact(Collection<T> facts) {
        final StringBuilder xml = new StringBuilder();
        final List<String> categories = new ArrayList<>();
        final List<String> tenantIds = new ArrayList<>();
        for (final T fact : facts) {
            categories.add(fact.getCategory());
            tenantIds.add(fact.getTenantId());
        }
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<Collection xmlns=\"http://schemas.microsoft.com/collection/metadata/2009\" SchemaVersion=\"1.0\" Name=\"FMS\">\n");
        xml.append("\t<FacetCategories>\n");
        for (final String category : categories) {
            xml.append("\t\t<FacetCategory Name=\"").append(category).append("\" Type=\"Number\"/>\n");
        }
        xml.append("\t</FacetCategories>\n");
        tenantIds.forEach(tenantId -> {
            xml.append("\n\t\t<Item Id=\"").append(tenantId).append("\" Img=\"#2\"")
                    .append(" Href=\"/usmo\" Name=\"").append(tenantId).append("\">").
                    append("\n\t\t\t<Facets>\n");
            facts.stream().filter(fact -> fact.getTenantId().equals(tenantId)).forEach(fact -> {
                xml.append("\t\t\t\t<Facet Name=\"").append(fact.getCategory()).append("\">\n");
                xml.append("\t\t\t\t\t<Number Value=\"").append(fact.getValue()).append("\"/>\n");
                xml.append("\t\t\t\t</Facet>\n");
            });
            xml.append("\t\t\t</Facets>\n\t\t</Item>\n");
        });
        xml.append("\t</Items>\n");
        xml.append("</Collection>");
        return xml.toString();
    }

    public String xmlFromFormRunnerFact(Collection<T> facts) throws IOException {
        final StringBuilder xml = new StringBuilder();
        final Set<String> categories = new HashSet<>();
        final Set<String> tenantIds = new HashSet<>();
        for (final T fact : facts) {
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
            xml.append("\n\t\t<Item Id=\"").append(tenantId).append("\" Img=\"#2\"")
                    .append(" Href=\"/usmo\" Name=\"").append(tenantId).append("\">").
                    append("\n\t\t\t<Facets>\n");
            facts.stream().filter(fact -> fact.getTenantId().equals(tenantId)).forEach(fact -> {
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
            xml.append("\t\t\t</Facets>\n\t\t</Item>\n");
        });
        xml.append("\t</Items>\n");
        xml.append("</Collection>");
        return xml.toString();
    }

    private FormRunnerValue getFormRunnerValueFromJson(Fact fact) throws JsonProcessingException {
        final ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(fact.getValue(), FormRunnerValue.class);
    }
}