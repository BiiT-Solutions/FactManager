package com.biit.factmanager.core.providers;

/*-
 * #%L
 * FactManager (core)
 * %%
 * Copyright (C) 2020 - 2025 BiiT Sourcing Solutions S.L.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.biit.factmanager.logger.FactManagerLogger;
import com.biit.factmanager.persistence.entities.Fact;
import com.biit.factmanager.persistence.entities.FormrunnerQuestionFact;
import com.biit.factmanager.persistence.entities.values.FormrunnerQuestionValue;
import com.biit.factmanager.persistence.enums.ChartType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Service
public class ChartProvider<T extends Fact<?>> {

    private static final String VERSION = "0.7.2";
    private final FactProvider<T> factProvider;

    @Autowired
    public ChartProvider(FactProvider<T> factProvider) {
        this.factProvider = factProvider;
    }


    public final String getChart(String organization, String unit, List<String> createdBy, String application, String tenant, String session, String subject,
                                 String group, String element, String elementName, String factType, String valueType, LocalDateTime startDate,
                                 LocalDateTime endDate,
                                 Integer lastDays, ChartType type, int page, int size, Pair<String, Object>... valueParameters) {
        if (!organization.isEmpty() && tenant.isEmpty() && session.isEmpty() && subject.isEmpty() && group.isEmpty() && element.isEmpty()) {
            return htmlFromformrunnerQuestionFactsByQuestion(factProvider.findByOrganization(organization), type);
        }
        if (organization.isEmpty() && tenant.isEmpty() && session.isEmpty() && subject.isEmpty() && group.isEmpty() && element.isEmpty()) {
            return htmlFromformrunnerQuestionFactsByQuestion(factProvider.getAll(), type);
        }
        return htmlFromformrunnerQuestionFactsByQuestion(factProvider.
                findBy(organization, unit, createdBy, application, tenant, session, subject, group, element, elementName,
                        factType, startDate, endDate, lastDays, null, null, null, page, size, valueParameters), type);
    }

    public String htmlFromformrunnerQuestionFactsByQuestion(Collection<T> facts, ChartType type) {
        final StringBuilder html = new StringBuilder();

        if (facts.isEmpty()) {
            FactManagerLogger.errorMessage(this.getClass().getName(), "Empty collection of facts, cannot create chart html");
            throw new NullPointerException();
        }
        final Collection<T> formrunnerQuestionFacts = filterNonFormrunnerQuestionFacts(facts);

        html.append("<!DOCTYPE html>\n")
                .append("<html lang=\"en\">\n")
                .append("<head>\n")
                .append("<meta charset=\"utf-8\"> </meta>\n")
                .append("<title>C3</title>\n")
                .append("<link href=\"https://cdnjs.cloudflare.com/ajax/libs/c3/").append(VERSION).append("/c3.css \" rel=\"stylesheet\"> </link>")
                .append("</head>\n\n")
                .append("<body>\n")
                .append("<div id=\"chart\"></div>\n\n")
                .append("<script src=\"https://d3js.org/d3.v5.min.js\"></script>\n")
                .append("<script src=\"https://cdnjs.cloudflare.com/ajax/libs/c3/").append(VERSION).append("/c3.min.js \"></script>\n");
        html.append("<script>");
        html.append("var chart = c3.generate({\n")
                .append("bindto: '#chart',\n")
                .append("data: {\n")
                .append("columns: [\n");

        final List<String> uniqueTenants = getUniqueTenants(formrunnerQuestionFacts);
        uniqueTenants.forEach(tenant -> {
            html.append("   [").append("'").append(tenant).append("', ");
            formrunnerQuestionFacts.forEach(formrunnerQuestionFact -> {
                if (formrunnerQuestionFact.getTenant().compareTo(tenant) == 0) {
                    final FormrunnerQuestionValue formrunnerQuestionValue = (FormrunnerQuestionValue) formrunnerQuestionFact.getEntity();
                    if (formrunnerQuestionValue.getAnswer() != null && formrunnerQuestionValue.getAnswer().matches("[+-]?\\d*(\\.\\d+)?")) {
                        html.append(formrunnerQuestionValue.getAnswer());
                        if (!Objects.equals(formrunnerQuestionFacts.stream().reduce((first, second) -> second).orElse(null), formrunnerQuestionFact)) {
                            html.append(", ");
                        } else {
                            FactManagerLogger.info(this.getClass().getName(), "Null or not number answer");
                        }
                    }
                }
            });
            html.append("]");
            if (!Objects.equals(uniqueTenants.stream().reduce((first, second) -> second).orElse(null), tenant)) {
                html.append(",\n");
            }
        });
        html.append("\n],\n").append("type: '").append(translateType(type)).append("'\n},")
                .append("\n axis: {")
                .append("\n x: {\n type: 'category',\n categories: [");

        getUniqueQuestions(formrunnerQuestionFacts).forEach(question -> html.append("'").append(question).append("',"));
        html.append("]\n }\n }")
                .append("\n});\n</script>\n</body>\n</html>");

        return html.toString();
    }

    private List<String> getUniqueTenants(Collection<T> facts) {
        final ArrayList<String> tenants = new ArrayList<>();
        facts.forEach(fact -> tenants.add(fact.getTenant()));
        return tenants.stream().distinct().toList();
    }

    private List<String> getUniqueQuestions(Collection<T> formrunnerQuestionFacts) {
        final List<String> questions = new ArrayList<>();
        formrunnerQuestionFacts.forEach(formrunnerQuestionFact -> {
            final FormrunnerQuestionValue formrunnerQuestionValue = (FormrunnerQuestionValue) formrunnerQuestionFact.getEntity();
            questions.add(formrunnerQuestionValue.getQuestion());
        });
        return questions.stream().distinct().toList();
    }

    private Collection<T> filterNonFormrunnerQuestionFacts(Collection<T> facts) {
        facts.removeIf(i -> !(i instanceof FormrunnerQuestionFact));
        return facts;
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
