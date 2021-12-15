package com.biit.factmanager.rest.api;

import com.biit.factmanager.core.providers.FactProvider;
import com.biit.factmanager.logger.FactManagerLogger;
import com.biit.factmanager.persistence.entities.Fact;
import com.biit.factmanager.persistence.entities.FormRunnerFact;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.BadRequestException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public abstract class FactServices<T extends Fact<?>> {

    private final FactProvider<T> factProvider;

    public FactServices(FactProvider<T> factProvider) {
        this.factProvider = factProvider;
    }

    @ApiOperation(value = "Adds a new fact", notes = "Parameters:\n"
            + "fact (required): Fact object to be added")
    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public T addFact(@ApiParam(value = "Notification Request", required = true) @RequestBody T fact,
                     HttpServletRequest httpRequest) {
        FactManagerLogger.info(this.getClass().getName(), "Adding fact '" + fact + "'.");
        return factProvider.save(fact);
    }

    @ApiOperation(value = "Save a list of facts", notes = "Parameters:\n"
            + "facts (required): List of Fact objects to be added")
    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping(value = "/collection", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<T> addFactList(@ApiParam(value = "Notification Request", required = true) @RequestBody List<T> facts,
                               HttpServletRequest httpRequest) {
        FactManagerLogger.debug(this.getClass().getName(), "Saving a list of facts '{}'.", facts);
        return factProvider.save(facts);
    }

    @ApiOperation(value = "Deletes a fact", notes = "Parameters:\n"
            + "fact (required): Fact object to be removed.")
    @ResponseStatus(value = HttpStatus.OK)
    @DeleteMapping(value = "")
    public void deleteFact(@ApiParam(value = "Fact entity", required = true) @RequestBody T fact,
                           HttpServletRequest httpRequest) {
        FactManagerLogger.info(this.getClass().getName(), "Remove fact");
        factProvider.delete(fact);
    }

    @ApiOperation(value = "Search facts functionality", notes = "Parameters:\n"
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
            @ApiParam(value = "tenantId", required = false) @RequestParam(value = "tenantId", required = false) String tenantId,
            @ApiParam(value = "organizationId", required = false) @RequestParam(value = "organizationId", required = false) String organizationId,
            @ApiParam(value = "tag", required = false) @RequestParam(value = "tag", required = false) String tag,
            @ApiParam(value = "group", required = false) @RequestParam(value = "group", required = false) String group,
            @ApiParam(value = "elementId", required = false) @RequestParam(value = "elementId", required = false) String elementId,
            @ApiParam(value = "startDate", required = false) @RequestParam(value = "startDate", required = false) LocalDateTime startDate,
            @ApiParam(value = "endDate", required = false) @RequestParam(value = "endDate", required = false) LocalDateTime endDate,
            @ApiParam(value = "lastDays", required = false) @RequestParam(value = "lastDays", required = false) Integer lastDays,
            @ApiParam(value = "parameters", required = false) @RequestParam(value = "parameters", required = false) List<String> valueParameters) {

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
