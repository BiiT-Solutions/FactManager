package com.biit.factmanager.test;

import com.biit.factmanager.core.providers.ChartProvider;
import com.biit.factmanager.core.providers.FactProvider;
import com.biit.factmanager.persistence.entities.FormRunnerFact;
import com.biit.factmanager.persistence.entities.values.FormRunnerValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
@Test(groups = {"facts"})
public class ChartProviderTest {

    @Autowired
    private FactProvider<FormRunnerFact> factProvider;

    @Autowired
    private ChartProvider<FormRunnerFact> chartProvider;

    private final List<FormRunnerFact> formRunnerFacts = new ArrayList<>();

    @BeforeClass
    public void populate() {
        for(int j = 0; j <= 2; j++) {
            for(int i = 0; i < 5; i++) {
                FormRunnerFact formRunnerFact = new FormRunnerFact();
                FormRunnerValue formRunnerValue = new FormRunnerValue();

                formRunnerValue.setScore((double) (i+j));
                formRunnerFact.setTenantId("persona" + j);
                formRunnerFact.setEntity(formRunnerValue);

                formRunnerFacts.add(formRunnerFact);
            }
        }

    }

    @Test
    public void htmlFromFormrunnerFacts() {
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

        getUniqueTenants().forEach(tenant -> {
            html.append("[ '" + tenant + "'");
            formRunnerFacts.forEach(formRunnerFact -> {
                if (formRunnerFact.getTenantId().compareTo(tenant) == 0) {
                    html.append(", " + formRunnerFact.getEntity().getScore());
                }
            });
            html.append("],\n");
        });

        html.append("],\n" +
                "              axes: {\n" +
                "                data2: 'y2'\n" +
                "              },\n" +
                "              type: '"+ type + "'\n" +
                "},\n" +
                "   axis: {\n" +
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

        System.out.println(html);
    }


    private  List<String> getUniqueTenants() {
        List<String> tenants = new ArrayList<>();
        formRunnerFacts.forEach(formRunnerFact -> tenants.add(formRunnerFact.getTenantId()));
        return tenants.stream().distinct().collect(Collectors.toList());
    }
}
