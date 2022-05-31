package com.biit.factmanager.core.providers;

import com.biit.factmanager.logger.FactManagerLogger;
import com.biit.factmanager.persistence.entities.Fact;
import com.biit.factmanager.persistence.entities.values.FormrunnerQuestionValue;
import com.biit.factmanager.persistence.enums.ChartType;
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

    private static final String VERSION = "0.7.2";
    private final FactProvider<T> factProvider;

    @Autowired
    public ChartProvider(FactProvider<T> factProvider) {
        this.factProvider = factProvider;
    }

    @SafeVarargs
    public final String getChart(String organizationId, String tenantId, String tag, String group, String elementId,
                            LocalDateTime startDate, LocalDateTime endDate, Integer lastDays, ChartType type,
                            Pair<String, Object>... valueParameters) {
        return htmlFromformrunnerQuestionFactsByQuestion(factProvider.
                findBy(organizationId, tenantId, tag, group, elementId, startDate, endDate, lastDays, valueParameters), type);
    }

    public String htmlFromformrunnerQuestionFactsByQuestion(Collection<T> formrunnerQuestionFacts, ChartType type) {
        final StringBuilder html = new StringBuilder();

        if (formrunnerQuestionFacts.isEmpty()) {
            FactManagerLogger.errorMessage(this.getClass().getName(), "Empty collection of facts, cannot create chart html");
            throw new NullPointerException();
        }

        html.append("<!DOCTYPE html>\n")
                .append("<html lang=\"en\">\n")
                .append("<head>\n")
                .append("<meta charset=\"utf-8\"> </meta>\n")
                .append("<title>C3</title>\n")
                .append("<link href=\"https://cdnjs.cloudflare.com/ajax/libs/c3/").append(VERSION).append("/c3.css \" rel=\"stylesheet\"> </link>")
                .append("</head>\n\n")
                .append("<body>\n")
                .append("<h1>").append(formrunnerQuestionFacts.stream().findFirst().get().getClass().getName()
                        .replaceAll("com.biit.factmanager.persistence.entities.", "")).append("</h1>\n")
                .append("<div id=\"chart\"></div>\n\n")
                .append("<script src=\"https://d3js.org/d3.v5.min.js\"></script>\n")
                .append("<script src=\"https://cdnjs.cloudflare.com/ajax/libs/c3/").append(VERSION).append("/c3.min.js \"></script>\n");
        html.append("<script>");
        html.append("var chart = c3.generate({\n")
                .append("bindto: '#chart',\n")
                .append("data: {\n")
                .append("columns: [");

        getUniqueTenants(formrunnerQuestionFacts).forEach(tenant -> {
            html.append("[ '").append(tenant).append("'");
            formrunnerQuestionFacts.forEach(formrunnerQuestionFact -> {
                if (formrunnerQuestionFact.getTenantId().compareTo(tenant) == 0) {
                    final FormrunnerQuestionValue formrunnerQuestionValue = (FormrunnerQuestionValue) formrunnerQuestionFact.getEntity();
                    if ((formrunnerQuestionValue.getScore() != null) && !(formrunnerQuestionValue.getScore().isNaN())) {
                        html.append(", ").append(formrunnerQuestionValue.getScore());
                    }
                }
            });
            html.append("],\n");
        });

        html.append("],\n").append("type: '").append(translateType(type)).append("'\n},")
                .append("\n axis: {")
                .append("\n x: {\n type: 'category',\n categories: [");

        getUniqueQuestions(formrunnerQuestionFacts).forEach(question -> html.append("'").append(question).append("',"));
        html.append("]\n }\n }")
                .append("\n});\n</script>\n</body>\n</html>");

        return html.toString();
    }

    private List<String> getUniqueTenants(Collection<T> facts) {
        final ArrayList<String> tenantIds = new ArrayList<>();
        facts.forEach(fact -> tenantIds.add(fact.getTenantId()));
        return tenantIds.stream().distinct().collect(Collectors.toList());
    }

    private List<String> getUniqueQuestions(Collection<T> formrunnerQuestionFacts) {
        final List<String> questions = new ArrayList<>();
        formrunnerQuestionFacts.forEach(formrunnerQuestionFact -> {
            final FormrunnerQuestionValue formrunnerQuestionValue = (FormrunnerQuestionValue) formrunnerQuestionFact.getEntity();
            questions.add(formrunnerQuestionValue.getQuestion());
        });
        return questions.stream().distinct().collect(Collectors.toList());
    }

    private String translateType(ChartType chartType) {
        switch (chartType) {
            case LINE:
                return "line";
            case STEP:
                return "step";
            default:
                return "bar";
        }
    }
}
