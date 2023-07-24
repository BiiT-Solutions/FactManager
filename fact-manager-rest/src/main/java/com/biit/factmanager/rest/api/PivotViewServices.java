package com.biit.factmanager.rest.api;

import com.biit.factmanager.core.providers.PivotViewProvider;
import com.biit.factmanager.persistence.entities.Fact;
import com.biit.factmanager.rest.exceptions.BadRequestException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RequestMapping(value = "/pivotView")
@RestController
public class PivotViewServices<T extends Fact<?>> {

    private final PivotViewProvider<T> pivotViewProvider;

    @Autowired
    public PivotViewServices(PivotViewProvider<T> pivotViewProvider) {
        this.pivotViewProvider = pivotViewProvider;
    }

    @Operation(summary = "Returns a pivotView view", description = """
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
            - type: possible types are BAR, LINE, STEP
            - customProperties: map of properties that are specific for each fact (search in custom properties)
            - parameters: set of parameters/value pairs that are specific for each fact (search in the value)",
            """, security = @SecurityRequirement(name = "bearerAuth"))
    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(value = "", produces = MediaType.APPLICATION_XML_VALUE)
    public String getView(
            HttpServletRequest httpRequest,
            @Parameter(name = "organization", required = false) @RequestParam(value = "organization", required = false) String organization,
            @Parameter(name = "issuer", required = false) @RequestParam(value = "issuer", required = false) String issuer,
            @Parameter(name = "application", required = false) @RequestParam(value = "application", required = false) String application,
            @Parameter(name = "tenant", required = false) @RequestParam(value = "tenant", required = false) String tenant,
            @Parameter(name = "session", required = false) @RequestParam(value = "session", required = false) String session,
            @Parameter(name = "subject", required = false) @RequestParam(value = "subject", required = false) String subject,
            @Parameter(name = "group", required = false) @RequestParam(value = "group", required = false) String group,
            @Parameter(name = "element", required = false) @RequestParam(value = "element", required = false) String element,
            @Parameter(name = "factType", required = false) @RequestParam(value = "factType", required = false) String factType,
            @Parameter(name = "startDate", required = false) @RequestParam(value = "startDate", required = false) LocalDateTime startDate,
            @Parameter(name = "endDate", required = false) @RequestParam(value = "endDate", required = false) LocalDateTime endDate,
            @Parameter(name = "lastDays", required = false) @RequestParam(value = "lastDays", required = false) Integer lastDays,
            @Parameter(name = "parameters", required = false) @RequestParam(value = "parameters", required = false) List<String> valueParameters) {

        if (valueParameters.size() == 1 && valueParameters.get(0).compareTo("[]") == 0) {
            valueParameters.remove(0);
        }

        if (valueParameters.size() % 2 == 1) {
            throw new BadRequestException("Invalid number of parameters.");
        }

        final Pair<String, Object>[] pairs = new Pair[valueParameters.size() / 2];
        for (int i = 0; i < valueParameters.size(); i += 2) {
            pairs[i] = Pair.of(valueParameters.get(i), valueParameters.get(i + 1));
        }

        return pivotViewProvider.get(organization, issuer, application, tenant, session, subject, group, element,
                factType, startDate, endDate, lastDays, pairs);
    }


}
