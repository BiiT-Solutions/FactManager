package com.biit.factmanager.rest.api;

import com.biit.factmanager.core.providers.FactProvider;
import com.biit.factmanager.logger.FactManagerLogger;
import com.biit.factmanager.persistence.entities.Fact;
import com.biit.factmanager.rest.exceptions.BadRequestException;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.util.Pair;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.List;

public abstract class FactServices<V, T extends Fact<V>> {

    private final FactProvider<T> factProvider;

    public FactServices(FactProvider<T> factProvider) {
        this.factProvider = factProvider;
    }

    @Operation(summary = "Adds a new fact", description = "Parameters:\n"
            + "fact (required): Fact object to be added", security = @SecurityRequirement(name = "bearerAuth"))
    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public T addFact(@Parameter(name = "Notification Request", required = true) @RequestBody T fact,
                     HttpServletRequest httpRequest) {
        FactManagerLogger.info(this.getClass().getName(), "Adding fact '" + fact + "'.");
        return factProvider.save(fact);
    }

    @Operation(summary = "Save a list of facts", description = "Parameters:\n"
            + "facts (required): List of Fact objects to be added", security = @SecurityRequirement(name = "bearerAuth"))
    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping(value = "/collection", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Collection<T> addFactList(@Parameter(name = "Fact list", required = true) @RequestBody List<T> facts,
                                     HttpServletRequest httpRequest) throws JsonProcessingException {
        FactManagerLogger.debug(this.getClass().getName(), "Saving a list of facts '{}'.", facts);
        return factProvider.save(facts);
    }

    @Operation(summary = "Deletes a fact", description = "Parameters:\n"
            + "fact (required): Fact object to be removed.", security = @SecurityRequirement(name = "bearerAuth"))
    @ResponseStatus(value = HttpStatus.OK)
    @DeleteMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void deleteFact(@Parameter(name = "fact", required = true) @RequestBody T fact,
                           HttpServletRequest httpRequest) {
        FactManagerLogger.info(this.getClass().getName(), "Remove fact");
        factProvider.delete(fact);
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
    public Collection<T> getFacts(
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
        return factProvider.findBy(organization, customer, application, tenant, tag, group, element, process,
                from != null ? LocalDateTime.ofInstant(from.toInstant(), ZoneId.systemDefault()) : null,
                to != null ? LocalDateTime.ofInstant(to.toInstant(), ZoneId.systemDefault()) : null,
                lastDays, true, pairs);
    }

}
