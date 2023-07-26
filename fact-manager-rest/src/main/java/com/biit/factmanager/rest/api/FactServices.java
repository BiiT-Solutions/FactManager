package com.biit.factmanager.rest.api;

import com.biit.factmanager.core.controllers.FactController;
import com.biit.factmanager.dto.FactDTO;
import com.biit.factmanager.logger.FactManagerLogger;
import com.biit.factmanager.rest.exceptions.BadRequestException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.util.Pair;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@RequestMapping(value = "/")
@RestController
public class FactServices<V> {

    private final FactController<V> factController;

    public FactServices(FactController<V> factController) {
        this.factController = factController;
    }

    protected FactController<V> getFactController() {
        return factController;
    }

    @Operation(summary = "Deletes a fact", description = "Parameters:\n"
            + "fact (required): Fact object to be removed.", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasAnyAuthority(@securityService.adminPrivilege)")
    @ResponseStatus(value = HttpStatus.OK)
    @DeleteMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void deleteFact(@Parameter(name = "fact", required = true) @RequestBody FactDTO fact,
                           HttpServletRequest httpRequest) {
        FactManagerLogger.info(this.getClass().getName(), "Remove fact");
        factController.deleteById(fact.getId());
    }

    @Operation(summary = "Search facts functionality", description = """
            Parameters:
            - organization: which organization belongs to
            - issuer: whom generate the fact
            - application: which application is generating the facts
            - tenant: the tenant classifier
            - session: event session
            - subject: what is doing
            - group: grouping option for the facts
            - element: if of the element that actions the fact
            - factType: if has a form answer, is a timing event, etc.
            - startDate: filtering facts from this day
            - endDate: filtering facts to this day
            - lastDays: if set, replaces startDate and endDate
            - customProperties: map of properties that are specific for each fact (search in custom properties)
            - parameters: set of parameters/value pairs that are specific for each fact (search in the value)",
            """,
            security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<FactDTO> getFacts(
            @Parameter(name = "organization", required = false) @RequestParam(value = "organization", required = false) String organization,
            @Parameter(name = "issuer", required = false) @RequestParam(value = "issuer", required = false) String issuer,
            @Parameter(name = "application", required = false) @RequestParam(value = "application", required = false) String application,
            @Parameter(name = "tenant", required = false) @RequestParam(value = "tenant", required = false) String tenant,
            @Parameter(name = "session", required = false) @RequestParam(value = "session", required = false) String session,
            @Parameter(name = "subject", required = false) @RequestParam(value = "subject", required = false) String subject,
            @Parameter(name = "group", required = false) @RequestParam(value = "group", required = false) String group,
            @Parameter(name = "element", required = false) @RequestParam(value = "element", required = false) String element,
            @Parameter(name = "factType", required = false) @RequestParam(value = "process", required = false) String factType,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @Parameter(description = "Facts since the selected date", example = "2023-01-01T00:00:00.00Z")
            @RequestParam(value = "from", required = false) OffsetDateTime from,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @Parameter(description = "Facts until the selected date", example = "2023-01-31T23:59:59.99Z")
            @RequestParam(value = "to", required = false) OffsetDateTime to,
            @Parameter(name = "lastDays", required = false) @RequestParam(value = "lastDays", required = false) Integer lastDays,
            @Parameter(name = "custom-properties", required = false) @RequestParam(value = "custom-properties", required = false)
            Map<String, String> customProperties,
            @Parameter(name = "parameters", required = false) @RequestParam(value = "parameters", required = false) List<String> valueParameters,
            HttpServletRequest httpRequest) {

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
        return factController.findBy(organization, issuer, application, tenant, session, subject, group, element, factType,
                from != null ? LocalDateTime.ofInstant(from.toInstant(), ZoneId.systemDefault()) : null,
                to != null ? LocalDateTime.ofInstant(to.toInstant(), ZoneId.systemDefault()) : null,
                lastDays, null, customProperties, pairs);
    }

}
