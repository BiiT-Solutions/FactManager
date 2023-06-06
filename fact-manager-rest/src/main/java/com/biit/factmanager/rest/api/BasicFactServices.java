package com.biit.factmanager.rest.api;

import com.biit.factmanager.core.providers.FactProvider;
import com.biit.factmanager.persistence.entities.BasicFact;
import com.biit.factmanager.rest.exceptions.BadRequestException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.util.Pair;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.List;

@RequestMapping(value = "/basic")
@RestController
public class BasicFactServices extends FactServices<String, BasicFact> {

    @Autowired
    @Qualifier("basicFactProvider")
    private FactProvider<BasicFact> factProvider;

    public BasicFactServices(FactProvider<BasicFact> factProvider) {
        super(factProvider);
    }

    @Operation(summary = "Search facts functionality", description = "Parameters:\n"
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
            + "- parameters: set of parameters/value pairs that are specific for each fact\n",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<BasicFact> getFacts(
            HttpServletRequest httpRequest,
            @Parameter(name = "organization", required = false) @RequestParam(value = "organization", required = false) String organization,
            @Parameter(name = "customer", required = false) @RequestParam(value = "customer", required = false) String customer,
            @Parameter(name = "application", required = false) @RequestParam(value = "application", required = false) String application,
            @Parameter(name = "tenant", required = false) @RequestParam(value = "tenant", required = false) String tenant,
            @Parameter(name = "tag", required = false) @RequestParam(value = "tag", required = false) String tag,
            @Parameter(name = "group", required = false) @RequestParam(value = "group", required = false) String group,
            @Parameter(name = "element", required = false) @RequestParam(value = "element", required = false) String element,
            @Parameter(name = "process", required = false) @RequestParam(value = "process", required = false) String process,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @Parameter(description = "Facts since the selected date", example = "2023-01-01T00:00:00.00Z")
            @RequestParam(value = "from", required = false) OffsetDateTime from,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @Parameter(description = "Facts until the selected date", example = "2023-01-31T23:59:59.99Z")
            @RequestParam(value = "to", required = false) OffsetDateTime to,
            @Parameter(name = "lastDays", required = false) @RequestParam(value = "lastDays", required = false) Integer lastDays,
            @Parameter(name = "parameters", required = false) @RequestParam(value = "parameters", required = false) List<String> valueParameters) {

        final Pair<String, Object>[] pairs;
        if (valueParameters != null) {
            if (valueParameters.size() % 2 == 1) {
                throw new BadRequestException("Invalid number of parameters.");
            }

            pairs = new Pair[valueParameters.size() / 2];
            for (int i = 0; i < valueParameters.size(); i += 2) {
                pairs[i] = Pair.of(valueParameters.get(i), valueParameters.get(i + 1));
            }
        } else {
            pairs = null;
        }
        final Collection<BasicFact> facts = factProvider.findBy(organization, customer, application, tenant, tag, group, element, process,
                from != null ? LocalDateTime.ofInstant(from.toInstant(), ZoneId.systemDefault()) : null,
                to != null ? LocalDateTime.ofInstant(to.toInstant(), ZoneId.systemDefault()) : null,
                lastDays, true, pairs);

        return facts;
    }
}
