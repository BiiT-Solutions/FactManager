package com.biit.factmanager.core.providers;

import com.biit.factmanager.core.providers.exceptions.FactNotFoundException;
import com.biit.factmanager.logger.FactManagerLogger;
import com.biit.factmanager.persistence.entities.Fact;
import com.biit.factmanager.persistence.entities.FormrunnerQuestionFact;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class PivotViewProvider<T extends Fact<?>> {
    private final FactProvider<T> factProvider;

    @Autowired
    public PivotViewProvider(FactProvider<T> factProvider) {
        this.factProvider = factProvider;
    }

    public String get(String organization, String unit, List<String> customers, String application, String tenant, String session, String subject,
                      String group, String element, String elementName, String factType, LocalDateTime startDate, LocalDateTime endDate,
                      Integer lastDays, int page, int size, Pair<String, Object>... valueParameters) {
        if (organization.isEmpty() && tenant.isEmpty() && session.isEmpty() && subject.isEmpty() && group.isEmpty() && element.isEmpty()) {
            return xmlFormFacts(factProvider.getAll());
        }
        return xmlFormFacts(factProvider.findBy(organization, unit, customers, application, tenant, session, subject, group, element, elementName,
                factType, startDate, endDate, lastDays, null, null, null, page, size, valueParameters));
    }

    public String xmlFormFacts(Collection<T> facts) {
        final StringBuilder xml = new StringBuilder();
        final Set<String> elementsByItem = new LinkedHashSet<>();
        final Map<String, Collection<T>> tenantsIds = new HashMap<>();
        final Map<String, List<Integer>> imageIndexes = new HashMap<>();

        if (facts.isEmpty()) {
            FactManagerLogger.errorMessage(this.getClass().getName(), "Empty collection of facts, cannot create chart html");
            throw new NullPointerException();
        }
        facts.removeIf(i -> !(i instanceof FormrunnerQuestionFact));

        for (final T fact : facts) {
            elementsByItem.add(fact.getPivotViewerTag());
            tenantsIds.computeIfAbsent(fact.getTenant(), k -> new ArrayList<>());
            tenantsIds.get(fact.getTenant()).add(fact);
            if (fact.getPivotViewerItemImageIndex() != null) {
                imageIndexes.computeIfAbsent(fact.getTenant(), k -> new ArrayList<>());
                imageIndexes.get(fact.getTenant()).add(fact.getPivotViewerItemImageIndex());
            }
        }
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<Collection xmlns=\"http://schemas.microsoft.com/collection/metadata/2009\" SchemaVersion=\"1.0\" Name=\"FactManager\">\n");
        xml.append("    <FacetCategories>\n");
        for (final String elementByItem : elementsByItem) {
            xml.append("        <FacetCategory Name=\"").append(elementByItem).append("\" Type=\"Number\"/>\n");
        }
        xml.append("    </FacetCategories>\n");
        xml.append("    <Items ImgBase=\"").append("./factManager/fact_manager.dzc").append("\">\n");
        tenantsIds.keySet().forEach(tenant -> {
            try {
                //Get item information from form fact.
                final T scoreFacts = tenantsIds.get(tenant).stream().filter(f -> f.getPivotViewerItemImageIndex() != null).findAny().orElseThrow(() ->
                        new FactNotFoundException(this.getClass(), "No facts for tenant '" + tenant + "' with score."));
                xml.append("        <Item Id=\"").append(scoreFacts.getPivotViewerValueItemId()).append("\" Img=\"")
                        .append(Collections.min(imageIndexes.get(tenant))).append("\"")
                        .append(" Href=\"/usmo\" Name=\"").append(scoreFacts.getPivotViewerItemName()).append("\">\n");
                xml.append("            <Facets>\n");
                tenantsIds.get(tenant).forEach(fact -> {
                    if (fact.getPivotViewerTag() != null && fact.getPivotViewerValue() != null) {
                        xml.append("                <Facet Name=\"").append(fact.getPivotViewerTag()).append("\">\n");
                        xml.append("                    <Number Value=\"").append(fact.getPivotViewerValue()).append("\"/>\n");
                        xml.append("                </Facet>\n");
                    }
                });
                xml.append("            </Facets>\n");
                xml.append("        </Item>\n");
            } catch (FactNotFoundException e) {
                FactManagerLogger.warning(this.getClass().getName(), "No facts defined for tenant '" + tenant + "'.");
            }
        });
        xml.append("    </Items>\n");
        xml.append("</Collection>\n");
        return xml.toString();
    }

    private Collection<T> filterNonFormrunnerQuestionFacts(Collection<T> facts) {
        facts.removeIf(i -> !(i instanceof FormrunnerQuestionFact));
        return facts;
    }
}
