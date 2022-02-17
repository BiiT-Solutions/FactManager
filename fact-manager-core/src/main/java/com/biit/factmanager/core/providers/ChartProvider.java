package com.biit.factmanager.core.providers;

import com.biit.factmanager.persistence.entities.Fact;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChartProvider<T extends Fact<?>> {
    private final FactProvider<T> factProvider;

    @Autowired
    public ChartProvider(FactProvider<T> factProvider) {
        this.factProvider = factProvider;
    }

    public String get(String organizationId, String tenantId, String tag, String group, String elementId,
                      LocalDateTime startDate, LocalDateTime endDate, Integer lastDays, String type, Pair<String,
            Object>... valueParameters) {
        return htmlFromFacts(factProvider.findBy(organizationId, tenantId, tag, group, elementId,
                startDate, endDate, lastDays, valueParameters), type);
    }

    public String htmlFromFacts(Collection<T> facts, String type) {
        final StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n")
                .append("<html lang=\"en\">\n")
                .append("<head>\n")
                .append("<meta charset=\"utf-8\">\n")
                .append("<title>C3</title>\n")
                .append("</head>\n\n")
                .append("<body>\n")
                .append("<h1>C3 example</h1>\n")
                .append("<div id=\"chart\"></div>\n\n")
                .append("<script src=\"https://d3js.org/d3.v5.min.js\"></script>\n")
                .append("<script src=\"c3.min.js\"></script>\n");
        html.append("<script>");
        html.append("var chart = c3.generate({\n")
                .append("bindto: '#chart',\n")
                .append("data: {\n")
                .append("columns: [\n");

        //setting data
        getUniqueTenants(facts).forEach(tenantId -> {
            html.append("[ '").append(tenantId).append("'");
            facts.forEach(fact -> {
                if (fact.getTenantId().compareTo(tenantId) == 0) {
                    html.append(", ").append(fact.getValue()); //needs to be checked, not sure.
                }
            });
            html.append("],\n");
        });

        html.append("],\n")
                .append("axes: {\n")
                .append("data2: 'y2'\n")
                .append("},\n")
                .append("type: '" + type + "'\n},\n")
                .append("axis: {\n")
                .append("y2: {\n")
                .append("show: true,\n")
                .append("label: {\n")
                .append("text: 'Y2',\n")
                .append("position: 'outer-middle' //outer inner top bottom\n},\n")
                .append("tick: {\n")
                .append("format: d3.format(\"$\")\n}\n}\n}\n});\n</script>\n</body>\n</html>");

        return html.toString();
    }

    private List<String> getUniqueTenants(Collection<T> facts) {
        final ArrayList<String> tenantIds = new ArrayList<>();
        facts.forEach(fact -> tenantIds.add(fact.getTenantId()));
        return tenantIds.stream().distinct().collect(Collectors.toList());
    }
}
