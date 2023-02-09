package com.biit.factmanager.rest.api;

import com.biit.factmanager.core.providers.ChartProvider;
import com.biit.factmanager.persistence.entities.Fact;
import com.biit.factmanager.persistence.enums.ChartType;
import com.biit.factmanager.rest.exceptions.BadRequestException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@RequestMapping(value = "/chart")
@RestController
public class ChartServices<T extends Fact<?>> {

    @Autowired
    public ChartProvider<T> chartProvider;

    public ChartServices(ChartProvider<T> chartProvider) {
        this.chartProvider = chartProvider;
    }

    @Operation(summary = "Create html chart based on parameters", security = @SecurityRequirement(name = "bearerAuth"),
            description = "Parameters:\n"
                    + "- organization: which organization belongs to\n"
                    + "- customer: which customer is the owner of the application\n"
                    + "- application: which application is generating the facts\n"
                    + "- tenant: the tenant classifier\n"
                    + "- tag: kafka tag\n"
                    + "- group: grouping option for the facts\n"
                    + "- element: if of the element that actions the fact\n"
                    + "- startDate: filtering facts from this day\n"
                    + "- endDate: filtering facts to this day\n"
                    + "- lastDays: if set, replaces startDate and endDate\n"
                    + "- parameters: set of parameters/value pairs that are specific for each fact\n")
    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(value = "", produces = MediaType.TEXT_HTML_VALUE)
    public String getChart(
            HttpServletRequest httpRequest,
            @Parameter(description = "organization", required = false) @RequestParam(value = "organization", required = false) String organization,
            @Parameter(name = "customer", required = false) @RequestParam(value = "customer", required = false) String customer,
            @Parameter(name = "application", required = false) @RequestParam(value = "application", required = false) String application,
            @Parameter(description = "tenant", required = false) @RequestParam(value = "tenant", required = false) String tenant,
            @Parameter(description = "tag", required = false) @RequestParam(value = "tag", required = false) String tag,
            @Parameter(description = "group", required = false) @RequestParam(value = "group", required = false) String group,
            @Parameter(description = "element", required = false) @RequestParam(value = "element", required = false) String element,
            @Parameter(description = "process", required = false) @RequestParam(value = "process", required = false) String process,
            @Parameter(description = "startDate", required = false) @RequestParam(value = "startDate", required = false) LocalDateTime startDate,
            @Parameter(description = "endDate", required = false) @RequestParam(value = "endDate", required = false) LocalDateTime endDate,
            @Parameter(description = "lastDays", required = false) @RequestParam(value = "lastDays", required = false) Integer lastDays,
            @Parameter(description = "possible types: BAR, LINE, STEP ", required = false) @RequestParam(value = "type", required = false) ChartType type,
            @Parameter(description = "parameters", required = false) @RequestParam(value = "parameters", required = false) List<String> valueParameters) {

        if (valueParameters.size() % 2 == 1) {
            throw new BadRequestException("Invalid number of parameters.");
        }

        final Pair<String, Object>[] pairs = new Pair[valueParameters.size() / 2];
        for (int i = 0; i < valueParameters.size(); i += 2) {
            pairs[i] = Pair.of(valueParameters.get(i), valueParameters.get(i + 1));
        }

        return chartProvider.getChart(organization, customer, application, tenant, tag, group, element, process, startDate, endDate, lastDays, type, pairs);
    }
}
