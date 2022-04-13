package com.biit.factmanager.rest.api;

import com.biit.factmanager.core.providers.FactProvider;
import com.biit.factmanager.logger.FactManagerLogger;
import com.biit.factmanager.persistence.entities.Fact;
import com.biit.factmanager.rest.exceptions.BadRequestException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public abstract class FactServices<T extends Fact<?>> {

    private final FactProvider<T> factProvider;

    public FactServices(FactProvider<T> factProvider) {
        this.factProvider = factProvider;
    }

    @Operation(summary = "Adds a new fact", description = "Parameters:\n"
            + "fact (required): Fact object to be added")
    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public T addFact(@Parameter(name = "Notification Request", required = true) @RequestBody T fact,
                     HttpServletRequest httpRequest) {
        FactManagerLogger.info(this.getClass().getName(), "Adding fact '" + fact + "'.");
        return factProvider.save(fact);
    }

    @Operation(summary = "Save a list of facts", description = "Parameters:\n"
            + "facts (required): List of Fact objects to be added")
    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping(value = "/collection", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<T> addFactList(@Parameter(name = "Fact list", required = true) @RequestBody List<T> facts,
                               HttpServletRequest httpRequest) {
        FactManagerLogger.debug(this.getClass().getName(), "Saving a list of facts '{}'.", facts);
        return factProvider.save(facts);
    }

    @Operation(summary = "Deletes a fact", description = "Parameters:\n"
            + "fact (required): Fact object to be removed.")
    @ResponseStatus(value = HttpStatus.OK)
    @DeleteMapping(value = "")
    public void deleteFact(@Parameter(name = "fact", required = true) @RequestBody T fact,
                           HttpServletRequest httpRequest) {
        FactManagerLogger.info(this.getClass().getName(), "Remove fact");
        factProvider.delete(fact);
    }

    @Operation(summary = "Search facts functionality", description = "Parameters:\n"
            + "- tenantId: the tenant classifier\n"
            + "- organizationId: which organization belongs to\n"
            + "- tag: kafka tag\n"
            + "- group: grouping option for the facts\n"
            + "- elementId: if of the element that actions the fact\n"
            + "- startDate: filtering facts from this day\n"
            + "- endDate: filtering facts to this day\n"
            + "- lastDays: if set, replaces startDate and endDate\n"
            + "- parameters: set of parameters/value pairs that are specific for each fact\n")
    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<T> getFacts(
            HttpServletRequest httpRequest,
            @Parameter(name = "tenantId", required = false) @RequestParam(value = "tenantId", required = false) String tenantId,
            @Parameter(name = "organizationId", required = false) @RequestParam(value = "organizationId", required = false) String organizationId,
            @Parameter(name = "tag", required = false) @RequestParam(value = "tag", required = false) String tag,
            @Parameter(name = "group", required = false) @RequestParam(value = "group", required = false) String group,
            @Parameter(name = "elementId", required = false) @RequestParam(value = "elementId", required = false) String elementId,
            @Parameter(name = "startDate", required = false) @RequestParam(value = "startDate", required = false) LocalDateTime startDate,
            @Parameter(name = "endDate", required = false) @RequestParam(value = "endDate", required = false) LocalDateTime endDate,
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
            return factProvider.findBy(organizationId, tenantId, tag, group, elementId, startDate, endDate, lastDays, pairs);
        } else {
            return factProvider.findBy(organizationId, tenantId, tag, group, elementId, startDate, endDate, lastDays);
        }
    }

}
