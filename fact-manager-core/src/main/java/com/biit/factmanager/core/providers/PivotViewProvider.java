package com.biit.factmanager.core.providers;

import com.biit.factmanager.core.providers.exceptions.FactNotFoundException;
import com.biit.factmanager.logger.FactManagerLogger;
import com.biit.factmanager.persistence.entities.Fact;
import com.biit.factmanager.persistence.repositories.FactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class PivotViewProvider<T extends Fact<?>> {
    private final FactRepository<T> factRepository;

    @Autowired
    public PivotViewProvider(FactRepository<T> factRepository) {
        this.factRepository = factRepository;
    }

    public String get(String organizationId, String tenantId, String tag, String group, String elementId, LocalDateTime startDate,
                      LocalDateTime endDate, Integer lastDays) throws
            FactNotFoundException {
        Collection<T> facts;
        if (lastDays == null) {
            facts = getAll(organizationId, tenantId, tag, group, elementId, startDate, endDate);
        } else {
            final LocalDateTime localStartDate = LocalDateTime.now().minusDays(lastDays);
            final LocalDateTime localEndDate = LocalDateTime.now();
            facts = getAll(organizationId, tenantId, tag, group, elementId, localStartDate, localEndDate);
        }
        return xmlFormFacts(facts);
    }

    public Collection<T> getAll(String organizationId, String tenantId, String tag, String group, String elementId, LocalDateTime startDate,
                                LocalDateTime endDate) {
        return factRepository.findBy(organizationId, tenantId, tag, group, elementId, startDate, endDate);
    }

    public String xmlFormFacts(Collection<T> facts) {
        final StringBuilder xml = new StringBuilder();
        final Set<String> elementsByItem = new LinkedHashSet<>();
        final Map<String, Collection<T>> tenantsIds = new HashMap<>();
        final Map<String, Integer> imageIndexes = new HashMap<>();
        for (final T fact : facts) {
            elementsByItem.add(fact.getPivotViewerTag());
            tenantsIds.computeIfAbsent(fact.getTenantId(), k -> new ArrayList<>());
            tenantsIds.get(fact.getTenantId()).add(fact);
            if (fact.getPivotViewerItemImageIndex() != null) {
                imageIndexes.put(fact.getTenantId(), fact.getPivotViewerItemImageIndex());
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
        tenantsIds.keySet().forEach(tenantId -> {
            try {
                //Get item information from form fact.
                final T scoreFacts = tenantsIds.get(tenantId).stream().filter(f -> f.getPivotViewerItemImageIndex() != null).findAny().orElseThrow(() ->
                        new FactNotFoundException(this.getClass(), "No facts for tenant '" + tenantId + "' with score."));
                xml.append("        <Item Id=\"").append(scoreFacts.getPivotViewerValueItemId()).append("\" Img=\"")
                        .append(imageIndexes.get(tenantId)).append("\"")
                        .append(" Href=\"/usmo\" Name=\"").append(scoreFacts.getPivotViewerItemName()).append("\">\n");
                xml.append("            <Facets>\n");
                tenantsIds.get(tenantId).forEach(fact -> {
                    if (fact.getPivotViewerTag() != null) {
                        xml.append("                <Facet Name=\"").append(fact.getPivotViewerTag()).append("\">\n");
                        xml.append("                    <Number Value=\"").append(fact.getPivotViewerValue()).append("\"/>\n");
                        xml.append("                </Facet>\n");
                    }
                });
                xml.append("            </Facets>\n");
                xml.append("        </Item>\n");
            } catch (FactNotFoundException e) {
                FactManagerLogger.warning(this.getClass().getName(), "No facts defined for tenantId '" + tenantId + "'.");
            }
        });
        xml.append("    </Items>\n");
        xml.append("</Collection>\n");
        return xml.toString();
    }
}
