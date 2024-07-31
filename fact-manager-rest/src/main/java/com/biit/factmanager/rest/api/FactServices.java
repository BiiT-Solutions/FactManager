package com.biit.factmanager.rest.api;

import com.biit.factmanager.core.controllers.FactController;
import com.biit.factmanager.dto.CustomPropertyDTO;
import com.biit.factmanager.dto.FactDTO;
import com.biit.factmanager.logger.FactManagerLogger;
import com.biit.server.exceptions.BadRequestException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.util.Pair;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping(value = "/facts")
@RestController
public class FactServices<V> {

    private final FactController<V> factController;

    public FactServices(FactController<V> factController) {
        this.factController = factController;
    }


    @Operation(summary = "Deletes a fact", description = "Parameters:\n"
            + "fact (required): Fact object to be removed.", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasAnyAuthority(@securityService.adminPrivilege)")
    @ResponseStatus(value = HttpStatus.OK)
    @PostMapping(value = "/delete", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void deleteFact(@Parameter(name = "fact", required = true) @RequestBody FactDTO fact,
                           HttpServletRequest httpRequest) {
        FactManagerLogger.info(this.getClass().getName(), "Removed fact '" + fact + "'.");
        factController.deleteById(fact.getId());
    }


    @PreAuthorize("hasAnyAuthority(@securityService.adminPrivilege)")
    @Operation(summary = "Deletes a fact.", security = @SecurityRequirement(name = "bearerAuth"))
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(value = "/delete/{id}")
    public void delete(@Parameter(description = "Id of an existing fact", required = true) @PathVariable("id") Long id,
                       HttpServletRequest request) {
        factController.deleteById(id);
    }


    @Operation(summary = "Search facts functionality", description = """
            Parameters:
            - organization: which organization belongs to.
            - createdBy: whom generate the fact.
            - application: which application is generating the facts.
            - tenant: the tenant classifier.
            - session: event session.
            - subject: what is doing.
            - group: grouping option for the facts.
            - element: The element that actions the fact.
            - elementName: The name of the entity. Can be the form name, a customer email, etc.
            - factType: if it has a form answer, is a timing event, etc.
            - valueType: the class name of the value.
            - startDate: filtering facts from this day.
            - endDate: filtering facts to this day.
            - lastDays: if set, replaces startDate and endDate.
            - latestByUser: only gets the latest fact by each different user.
            - parameters: set of parameters/value pairs that are specific for each fact (search in the value).
            """,
            security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasAnyAuthority(@securityService.editorPrivilege, @securityService.adminPrivilege)")
    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<FactDTO> getFacts(
            @Parameter(name = "organization", required = false) @RequestParam(value = "organization", required = false) String organization,
            @Parameter(name = "createdBy", required = false) @RequestParam(value = "createdBy", required = false) List<String> createdBy,
            @Parameter(name = "application", required = false) @RequestParam(value = "application", required = false) String application,
            @Parameter(name = "tenant", required = false) @RequestParam(value = "tenant", required = false) String tenant,
            @Parameter(name = "session", required = false) @RequestParam(value = "session", required = false) String session,
            @Parameter(name = "subject", required = false) @RequestParam(value = "subject", required = false) String subject,
            @Parameter(name = "group", required = false) @RequestParam(value = "group", required = false) String group,
            @Parameter(name = "element", required = false) @RequestParam(value = "element", required = false) String element,
            @Parameter(name = "elementName", required = false) @RequestParam(value = "elementName", required = false) String elementName,
            @Parameter(name = "factType", required = false) @RequestParam(value = "factType", required = false) String factType,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @Parameter(description = "Facts since the selected date", example = "2023-01-01T00:00:00.00Z")
            @RequestParam(value = "from", required = false) OffsetDateTime from,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @Parameter(description = "Facts until the selected date", example = "2023-01-31T23:59:59.99Z")
            @RequestParam(value = "to", required = false) OffsetDateTime to,
            @Parameter(name = "lastDays", required = false) @RequestParam(value = "lastDays", required = false) Integer lastDays,
            @Parameter(name = "latestByUser", required = false) @RequestParam(value = "latestByUser", required = false) Boolean latestByUser,
            @Parameter(name = "parameters", required = false) @RequestParam(value = "parameters", required = false) List<String> valueParameters,
            HttpServletRequest httpRequest) {

        final Pair<String, Object>[] pairs;
        if (valueParameters != null) {
            if (valueParameters.size() % 2 == 1) {
                throw new BadRequestException(this.getClass(), "Invalid number of parameters.");
            }

            pairs = new Pair[valueParameters.size() / 2];
            for (int i = 0; i < valueParameters.size(); i += 2) {
                pairs[i] = Pair.of(valueParameters.get(i), valueParameters.get(i + 1));
            }
        } else {
            pairs = null;
        }

        return factController.findBy(organization, createdBy, application, tenant, session, subject, group, element, elementName,
                factType,
                from != null ? LocalDateTime.ofInstant(from.toInstant(), ZoneId.systemDefault()) : null,
                to != null ? LocalDateTime.ofInstant(to.toInstant(), ZoneId.systemDefault()) : null,
                lastDays, latestByUser, null, null, pairs);
    }

    @Operation(summary = "Search in your facts", description = """
            Parameters:
            - organization: which organization belongs to.
            - application: which application is generating the facts.
            - tenant: the tenant classifier.
            - session: event session.
            - subject: what is doing.
            - group: grouping option for the facts.
            - element: The element that actions the fact.
            - elementName: The name of the entity. Can be the form name, a customer email, etc.
            - factType: if it has a form answer, is a timing event, etc.
            - valueType: the class name of the value.
            - startDate: filtering facts from this day.
            - endDate: filtering facts to this day.
            - lastDays: if set, replaces startDate and endDate.
            - latestByUser: only gets the latest fact by each different user.
            - parameters: set of parameters/value pairs that are specific for each fact (search in the value).
            """,
            security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(value = "/own", produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<FactDTO> getOwnFacts(
            @Parameter(name = "organization", required = false) @RequestParam(value = "organization", required = false) String organization,
            @Parameter(name = "application", required = false) @RequestParam(value = "application", required = false) String application,
            @Parameter(name = "tenant", required = false) @RequestParam(value = "tenant", required = false) String tenant,
            @Parameter(name = "session", required = false) @RequestParam(value = "session", required = false) String session,
            @Parameter(name = "subject", required = false) @RequestParam(value = "subject", required = false) String subject,
            @Parameter(name = "group", required = false) @RequestParam(value = "group", required = false) String group,
            @Parameter(name = "element", required = false) @RequestParam(value = "element", required = false) String element,
            @Parameter(name = "elementName", required = false) @RequestParam(value = "elementName", required = false) String elementName,
            @Parameter(name = "factType", required = false) @RequestParam(value = "factType", required = false) String factType,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @Parameter(description = "Facts since the selected date", example = "2023-01-01T00:00:00.00Z")
            @RequestParam(value = "from", required = false) OffsetDateTime from,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @Parameter(description = "Facts until the selected date", example = "2023-01-31T23:59:59.99Z")
            @RequestParam(value = "to", required = false) OffsetDateTime to,
            @Parameter(name = "lastDays", required = false) @RequestParam(value = "lastDays", required = false) Integer lastDays,
            @Parameter(name = "latestByUser", required = false) @RequestParam(value = "latestByUser", required = false) Boolean latestByUser,
            @Parameter(name = "parameters", required = false) @RequestParam(value = "parameters", required = false) List<String> valueParameters,
            Authentication authentication,
            HttpServletRequest httpRequest) {

        final Pair<String, Object>[] pairs;
        if (valueParameters != null) {
            if (valueParameters.size() % 2 == 1) {
                throw new BadRequestException(this.getClass(), "Invalid number of parameters.");
            }

            pairs = new Pair[valueParameters.size() / 2];
            for (int i = 0; i < valueParameters.size(); i += 2) {
                pairs[i] = Pair.of(valueParameters.get(i), valueParameters.get(i + 1));
            }
        } else {
            pairs = null;
        }
        return factController.findBy(organization, Collections.singletonList(authentication.getName()), application,
                tenant, session, subject, group, element, elementName,
                factType,
                from != null ? LocalDateTime.ofInstant(from.toInstant(), ZoneId.systemDefault()) : null,
                to != null ? LocalDateTime.ofInstant(to.toInstant(), ZoneId.systemDefault()) : null,
                lastDays, latestByUser, null, null, pairs);
    }


    @Operation(summary = "Search facts functionality", description = """
            Parameters:
            - organization: which organization belongs to.
            - createdBy: whom generate the fact.
            - application: which application is generating the facts.
            - tenant: the tenant classifier.
            - session: event session.
            - subject: what is doing.
            - group: grouping option for the facts.
            - element: The element that actions the fact.
            - elementName: The name of the entity. Can be the form name, a customer email, etc.
            - factType: if it has a form answer, is a timing event, etc.
            - valueType: the class name of the value.
            - startDate: filtering facts from this day.
            - endDate: filtering facts to this day.
            - lastDays: if set, replaces startDate and endDate.
            - latestByUser: only gets the latest fact by each different user.
            - parameters: set of parameters/value pairs that are specific for each fact (search in the value).
            """,
            security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasAnyAuthority(@securityService.editorPrivilege, @securityService.adminPrivilege)")
    @ResponseStatus(value = HttpStatus.OK)
    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<FactDTO> getFacts(
            @Parameter(name = "organization", required = false) @RequestParam(value = "organization", required = false) String organization,
            @Parameter(name = "createdBy", required = false) @RequestParam(value = "createdBy", required = false) List<String> createdBy,
            @Parameter(name = "application", required = false) @RequestParam(value = "application", required = false) String application,
            @Parameter(name = "tenant", required = false) @RequestParam(value = "tenant", required = false) String tenant,
            @Parameter(name = "session", required = false) @RequestParam(value = "session", required = false) String session,
            @Parameter(name = "subject", required = false) @RequestParam(value = "subject", required = false) String subject,
            @Parameter(name = "group", required = false) @RequestParam(value = "group", required = false) String group,
            @Parameter(name = "element", required = false) @RequestParam(value = "element", required = false) String element,
            @Parameter(name = "elementName", required = false) @RequestParam(value = "elementName", required = false) String elementName,
            @Parameter(name = "factType", required = false) @RequestParam(value = "factType", required = false) String factType,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @Parameter(description = "Facts since the selected date", example = "2023-01-01T00:00:00.00Z")
            @RequestParam(value = "from", required = false) OffsetDateTime from,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @Parameter(description = "Facts until the selected date", example = "2023-01-31T23:59:59.99Z")
            @RequestParam(value = "to", required = false) OffsetDateTime to,
            @Parameter(name = "lastDays", required = false) @RequestParam(value = "lastDays", required = false) Integer lastDays,
            @Parameter(name = "latestByUser", required = false) @RequestParam(value = "latestByUser", required = false) Boolean latestByUser,
            @RequestBody(required = false) Collection<CustomPropertyDTO> customProperties,
            @Parameter(name = "parameters", required = false) @RequestParam(value = "parameters", required = false) List<String> valueParameters,
            HttpServletRequest httpRequest) {

        final Pair<String, Object>[] pairs;
        if (valueParameters != null) {
            if (valueParameters.size() % 2 == 1) {
                throw new BadRequestException(this.getClass(), "Invalid number of parameters.");
            }

            pairs = new Pair[valueParameters.size() / 2];
            for (int i = 0; i < valueParameters.size(); i += 2) {
                pairs[i] = Pair.of(valueParameters.get(i), valueParameters.get(i + 1));
            }
        } else {
            pairs = null;
        }

        final Map<String, String> customPropertiesMap = new HashMap<>();
        if (customProperties != null) {
            customProperties.forEach(customPropertyDTO -> customPropertiesMap.put(customPropertyDTO.getKey(), customPropertyDTO.getValue()));
        }

        return factController.findBy(organization, createdBy, application, tenant, session, subject, group, element, elementName,
                factType,
                from != null ? LocalDateTime.ofInstant(from.toInstant(), ZoneId.systemDefault()) : null,
                to != null ? LocalDateTime.ofInstant(to.toInstant(), ZoneId.systemDefault()) : null,
                lastDays, latestByUser, null, customPropertiesMap, pairs);
    }


    @Operation(summary = "Search in your facts", description = """
            Parameters:
            - organization: which organization belongs to.
            - application: which application is generating the facts.
            - tenant: the tenant classifier.
            - session: event session.
            - subject: what is doing.
            - group: grouping option for the facts.
            - element: The element that actions the fact.
            - elementName: The name of the entity. Can be the form name, a customer email, etc.
            - factType: if it has a form answer, is a timing event, etc.
            - valueType: the class name of the value.
            - startDate: filtering facts from this day.
            - endDate: filtering facts to this day.
            - lastDays: if set, replaces startDate and endDate.
            - latestByUser: only gets the latest fact by each different user.
            - parameters: set of parameters/value pairs that are specific for each fact (search in the value).
            - customProperties: map of properties that are specific for each fact (search in custom properties).
            """,
            security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @ResponseStatus(value = HttpStatus.OK)
    @PostMapping(value = "/own", produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<FactDTO> getOwnFacts(
            @Parameter(name = "organization", required = false) @RequestParam(value = "organization", required = false) String organization,
            @Parameter(name = "application", required = false) @RequestParam(value = "application", required = false) String application,
            @Parameter(name = "tenant", required = false) @RequestParam(value = "tenant", required = false) String tenant,
            @Parameter(name = "session", required = false) @RequestParam(value = "session", required = false) String session,
            @Parameter(name = "subject", required = false) @RequestParam(value = "subject", required = false) String subject,
            @Parameter(name = "group", required = false) @RequestParam(value = "group", required = false) String group,
            @Parameter(name = "element", required = false) @RequestParam(value = "element", required = false) String element,
            @Parameter(name = "elementName", required = false) @RequestParam(value = "elementName", required = false) String elementName,
            @Parameter(name = "factType", required = false) @RequestParam(value = "factType", required = false) String factType,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @Parameter(description = "Facts since the selected date", example = "2023-01-01T00:00:00.00Z")
            @RequestParam(value = "from", required = false) OffsetDateTime from,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @Parameter(description = "Facts until the selected date", example = "2023-01-31T23:59:59.99Z")
            @RequestParam(value = "to", required = false) OffsetDateTime to,
            @Parameter(name = "lastDays", required = false) @RequestParam(value = "lastDays", required = false) Integer lastDays,
            @Parameter(name = "latestByUser", required = false) @RequestParam(value = "latestByUser", required = false) Boolean latestByUser,
            @Parameter(name = "parameters", required = false) @RequestParam(value = "parameters", required = false) List<String> valueParameters,
            @RequestBody(required = false) Collection<CustomPropertyDTO> customProperties,
            Authentication authentication,
            HttpServletRequest httpRequest) {

        final Pair<String, Object>[] pairs;
        if (valueParameters != null) {
            if (valueParameters.size() % 2 == 1) {
                throw new BadRequestException(this.getClass(), "Invalid number of parameters.");
            }

            pairs = new Pair[valueParameters.size() / 2];
            for (int i = 0; i < valueParameters.size(); i += 2) {
                pairs[i] = Pair.of(valueParameters.get(i), valueParameters.get(i + 1));
            }
        } else {
            pairs = null;
        }

        final Map<String, String> customPropertiesMap = new HashMap<>();
        if (customProperties != null) {
            customProperties.forEach(customPropertyDTO -> customPropertiesMap.put(customPropertyDTO.getKey(), customPropertyDTO.getValue()));
        }

        return factController.findBy(organization, Collections.singletonList(authentication.getName()), application,
                tenant, session, subject, group, element, elementName,
                factType,
                from != null ? LocalDateTime.ofInstant(from.toInstant(), ZoneId.systemDefault()) : null,
                to != null ? LocalDateTime.ofInstant(to.toInstant(), ZoneId.systemDefault()) : null,
                lastDays, latestByUser, null, customPropertiesMap, pairs);
    }

}
