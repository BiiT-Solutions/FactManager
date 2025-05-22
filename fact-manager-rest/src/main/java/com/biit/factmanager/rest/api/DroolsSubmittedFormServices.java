package com.biit.factmanager.rest.api;

import com.biit.drools.form.xls.exceptions.InvalidXlsElementException;
import com.biit.factmanager.core.controllers.DroolsFormXmlController;
import com.biit.factmanager.core.controllers.FactController;
import com.biit.factmanager.dto.FactDTO;
import com.biit.factmanager.logger.FactManagerLogger;
import com.biit.factmanager.persistence.entities.LogFact;
import com.biit.factmanager.rest.api.model.XmlSearch;
import com.biit.server.rest.SecurityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RequestMapping(value = "/facts/drools-forms")
@RestController
public class DroolsSubmittedFormServices {
    private static final String SUBJECT = "CREATED";
    private static final String FACT_TYPE = "DroolsResultForm";

    private final FactController<LogFact> factController;
    private final DroolsFormXmlController formXmlController;

    @Autowired
    private SecurityService securityService;

    public DroolsSubmittedFormServices(FactController<LogFact> factController, DroolsFormXmlController formXmlController) {
        this.factController = factController;
        this.formXmlController = formXmlController;
    }

    @Operation(summary = "Search forms functionality", description = """
            Parameters:
            - organization: which organization belongs to.
            - unit: if the fact is related to a team, department, ...
            - createdBy: whom generate the fact.
            - application: which application is generating the facts.
            - tenant: the tenant classifier.
            - session: event session.
            - group: grouping option for the facts.
            - element: The element that actions the fact.
            - elementName: The name of the form..
            - startDate: filtering facts from this day.
            - endDate: filtering facts to this day.
            - lastDays: if set, replaces startDate and endDate.
            - latestByUser: only gets the latest fact by each different user.
            """,
            security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasAnyAuthority(@securityService.editorPrivilege, @securityService.adminPrivilege)")
    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(value = "/xls", produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public byte[] getXmlDocument(
            @Parameter(name = "organization", required = false) @RequestParam(value = "organization", required = false) String organization,
            @Parameter(name = "unit", required = false) @RequestParam(value = "unit", required = false) String unit,
            @Parameter(name = "createdBy", required = false) @RequestParam(value = "createdBy", required = false) List<String> createdBy,
            @Parameter(name = "application", required = false) @RequestParam(value = "application", required = false) String application,
            @Parameter(name = "tenant", required = false) @RequestParam(value = "tenant", required = false) String tenant,
            @Parameter(name = "session", required = false) @RequestParam(value = "session", required = false) String session,
            @Parameter(name = "group", required = false) @RequestParam(value = "group", required = false) String group,
            @Parameter(name = "element", required = false) @RequestParam(value = "element", required = false) String element,
            @Parameter(name = "elementName", required = true) @RequestParam(value = "elementName") String elementName,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @Parameter(description = "Facts since the selected date", example = "2023-01-01T00:00:00.00Z")
            @RequestParam(value = "from", required = false) OffsetDateTime from,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @Parameter(description = "Facts until the selected date", example = "2023-01-31T23:59:59.99Z")
            @RequestParam(value = "to", required = false) OffsetDateTime to,
            @Parameter(name = "lastDays", required = false) @RequestParam(value = "lastDays", required = false) Integer lastDays,
            @Parameter(name = "latestByUser", required = false) @RequestParam(value = "latestByUser", required = false) Boolean latestByUser,
            HttpServletRequest httpRequest, HttpServletResponse response) throws InvalidXlsElementException {


        final Collection<FactDTO> facts = factController.findBy(organization, unit, createdBy, application, tenant, session, SUBJECT, group,
                element, elementName, FACT_TYPE,
                from != null ? LocalDateTime.ofInstant(from.toInstant(), ZoneId.systemDefault()) : null,
                to != null ? LocalDateTime.ofInstant(to.toInstant(), ZoneId.systemDefault()) : null,
                lastDays, latestByUser, null, null, null);

        FactManagerLogger.debug(this.getClass(), "Found '{}' facts.", facts.size());
        final byte[] bytes = formXmlController.convert(facts);
        final ContentDisposition contentDisposition = ContentDisposition.builder("attachment")
                .filename((elementName != null ? elementName : "values") + ".xls").build();
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString());
        return bytes;
    }


    @Operation(summary = "Search forms functionality", description = """
            Receives a list of requests and generate a XML document with the information of all involved forms. Can contain different form sources.
            """,
            security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasAnyAuthority(@securityService.editorPrivilege, @securityService.adminPrivilege)")
    @ResponseStatus(value = HttpStatus.OK)
    @PostMapping(value = "/xls", produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public byte[] getXmlDocument(
            @RequestBody List<XmlSearch> xmlSearchres,
            Authentication authentication, HttpServletRequest httpRequest, HttpServletResponse response) throws InvalidXlsElementException {

        final List<FactDTO> facts = new ArrayList<>();

        for (XmlSearch xmlSearch : xmlSearchres) {
            securityService.canBeDoneByDifferentUsers(xmlSearch.getCreatedBy(), authentication);

            facts.addAll(factController.findBy(xmlSearch.getOrganization(), xmlSearch.getUnit(), xmlSearch.getCreatedBy(),
                    xmlSearch.getApplication(), xmlSearch.getTenant(), xmlSearch.getSession(), SUBJECT,
                    xmlSearch.getGroup(), xmlSearch.getElement(), xmlSearch.getElementName(), FACT_TYPE,
                    xmlSearch.getFrom() != null ? LocalDateTime.ofInstant(xmlSearch.getFrom().toInstant(), ZoneId.systemDefault()) : null,
                    xmlSearch.getTo() != null ? LocalDateTime.ofInstant(xmlSearch.getTo().toInstant(), ZoneId.systemDefault()) : null,
                    xmlSearch.getLastDays(), xmlSearch.getLatestByUser(), null, null, null));
        }


        FactManagerLogger.debug(this.getClass(), "Found '{}' facts.", facts.size());
        final byte[] bytes = formXmlController.convert(facts);
        final ContentDisposition contentDisposition = ContentDisposition.builder("attachment")
                .filename("form.xls").build();
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString());
        return bytes;
    }


}
