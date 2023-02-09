package com.biit.factmanager.core.providers;

import com.biit.factmanager.core.providers.exceptions.FactNotFoundException;
import com.biit.factmanager.logger.FactManagerLogger;
import com.biit.factmanager.persistence.entities.Fact;
import com.biit.factmanager.persistence.entities.FormrunnerQuestionFact;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class PivotViewProvider<T extends Fact<?>> {
    private final FactProvider<T> factProvider;

    @Autowired
    public PivotViewProvider(FactProvider<T> factProvider) {
        this.factProvider = factProvider;
    }

    public String get(String organization, String customer, String application, String tenant, String tag, String group, String element, String process,
                      LocalDateTime startDate, LocalDateTime endDate, Integer lastDays, Pair<String, Object>... valueParameters) {
        if (organization.isEmpty() && tenant.isEmpty() && tag.isEmpty() && group.isEmpty() && element.isEmpty()) {
            return xmlFormFacts(factProvider.getAll());
        }
        return xmlFormFacts(factProvider.findBy(organization, customer, application, tenant, tag, group, element, process, startDate,
                endDate, lastDays, null, valueParameters));
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
