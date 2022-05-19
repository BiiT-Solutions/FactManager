package com.biit.factmanager.core.providers;

import com.biit.factmanager.persistence.entities.Fact;
import com.biit.factmanager.persistence.entities.FormRunnerFact;
import com.biit.factmanager.persistence.entities.values.FormRunnerValue;
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
                      LocalDateTime startDate, LocalDateTime endDate, Integer lastDays, String type, String version,
                      Pair<String, Object>... valueParameters) {

        return htmlFromFormrunnerFactsByQuestion(factProvider.getAll(), type, version);
    }

    public String htmlFromFormrunnerFactsByQuestion(Collection<T> formRunnerFacts, String type, String version) {
        final StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n")
                .append("<html lang=\"en\">\n")
                .append("<head>\n")
                .append("<meta charset=\"utf-8\"> </meta>\n")
                .append("<title>C3</title>\n")
                .append("<link href=\"https://cdnjs.cloudflare.com/ajax/libs/c3/").append(version).append("/c3.css \" rel=\"stylesheet\"> </link>")
                .append("</head>\n\n")
                .append("<body>\n")
                .append("<h1>").append(formRunnerFacts.stream().findFirst().get().getClass().getName()
                        .replaceAll("com.biit.factmanager.persistence.entities.", "")).append("</h1>\n")
                .append("<div id=\"chart\"></div>\n\n")
                .append("<script src=\"https://d3js.org/d3.v5.min.js\"></script>\n")
                .append("<script src=\"https://cdnjs.cloudflare.com/ajax/libs/c3/").append(version).append("/c3.min.js \"></script>\n");
        html.append("<script>");
        html.append("var chart = c3.generate({\n")
                .append("bindto: '#chart',\n")
                .append("data: {\n")
                .append("columns: [");

        getUniqueTenants(formRunnerFacts).forEach(tenant -> {
            html.append("[ '").append(tenant).append("'");
            formRunnerFacts.forEach(formRunnerFact -> {
                if (formRunnerFact.getTenantId().compareTo(tenant) == 0) {
                    final FormRunnerValue formRunnerValue = (FormRunnerValue) formRunnerFact.getEntity();
                    html.append(", ").append(formRunnerValue.getScore());
                }
            });
            html.append("],\n");
        });

        html.append("],\n")
                .append("type: '" + type + "'\n},")
                .append("\n axis: {")
                .append("\n x: {\n type: 'category',\n categories: [");

        getUniqueQuestions((Collection<FormRunnerFact>) formRunnerFacts).forEach(question -> {
            html.append("'").append(question).append("',");
        });
        html.append("]\n }\n }")
                .append("\n});\n</script>\n</body>\n</html>");

        return html.toString();
    }

    private List<String> getUniqueTenants(Collection<T> facts) {
        final ArrayList<String> tenantIds = new ArrayList<>();
        facts.forEach(fact -> tenantIds.add(fact.getTenantId()));
        return tenantIds.stream().distinct().collect(Collectors.toList());
    }

    private List<String> getUniqueQuestions(Collection<FormRunnerFact> formRunnerFacts) {
        final List<String> questions = new ArrayList<>();
        formRunnerFacts.forEach(formRunnerFact -> questions.add(formRunnerFact.getEntity().getQuestion()));
        return questions.stream().distinct().collect(Collectors.toList());
    }
}
