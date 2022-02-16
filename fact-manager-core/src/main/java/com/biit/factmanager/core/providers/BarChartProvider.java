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
public class BarChartProvider<T extends Fact<?>> {
    private final FactProvider<T> factProvider;

    @Autowired
    public BarChartProvider(FactProvider<T> factProvider) {
        this.factProvider = factProvider;
    }

    //Method that will be called by endpoint (barChartServices)
    public String get(String organizationId, String tenantId, String tag, String group, String elementId,
                      LocalDateTime startDate, LocalDateTime endDate, Integer lastDays, Pair<String,
            Object>... valueParameters) {
        return htmlFromFacts(factProvider.findBy(organizationId, tenantId, tag, group, elementId,
                startDate, endDate, lastDays, valueParameters));
    }

    //html that is going to be displayed based on facts
    public String htmlFromFacts(Collection<T> facts) {
        final String type = "bar";
        final StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "    <head>\n" +
                "        <meta charset=\"utf-8\">\n" +
                "        <title>C3</title>\n" +
                "    </head>\n" +
                "\n" +
                "    <body>\n" +
                "        <h1>C3 example</h1>\n" +
                "        <div id=\"chart\"></div>\n" +
                "\n" +
                "        <script src=\"https://d3js.org/d3.v5.min.js\"></script>\n" +
                "        <script src=\"c3.min.js\"></script>\n");
        html.append("<script>");
        html.append("var chart = c3.generate({\n" +
                "            bindto: '#chart',\n" +
                "            data: {\n" +
                "              columns: [\n");

        //setting data
        getUniqueTenants(facts).forEach(tenantId -> {
            html.append("[ '" + tenantId + "'");
            facts.forEach(fact -> {
                if (fact.getTenantId().compareTo(tenantId) == 0) {
                    html.append(", " + fact.getValue()); //needs to be checked, not sure.
                }
            });
            html.append("],\n");
        });

        html.append("],\n" +
                "              axes: {\n" +
                "                data2: 'y2' // ADD\n" +
                "              },\n" +
                "              type: '"+ type + "'\n" +
                "},\n" +
                "axis: {\n" +
                "              y2: {\n" +
                "                show: true,\n" +
                "                label: {\n" +
                "                    text: 'Y2',\n" +
                "                    position: 'outer-middle' //outer inner top bottom\n" +
                "                },\n" +
                "                tick: {\n" +
                "                    format: d3.format(\"$\")\n" +
                "                }\n" +
                "              }\n" +
                "            }\n" +
                "        });\n" +
                "        </script>\n" +
                "    </body>\n" +
                "</html>");


        return html.toString();
    }


    private List<String> getUniqueTenants(Collection<T> facts) {
        List<String> tenantIds = new ArrayList<>();
        facts.forEach(fact -> tenantIds.add(fact.getTenantId()));
        return tenantIds.stream().distinct().collect(Collectors.toList());
    }
}
