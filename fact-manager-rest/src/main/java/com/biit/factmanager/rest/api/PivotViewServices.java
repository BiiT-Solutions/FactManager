package com.biit.factmanager.rest.api;

import com.biit.factmanager.core.providers.PivotViewProvider;
import com.biit.factmanager.persistence.entities.Fact;
import com.biit.factmanager.rest.exceptions.BadRequestException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@RequestMapping(value = "/pivotView")
@RestController
public class PivotViewServices<T extends Fact<?>> {

    public PivotViewProvider<T> pivotViewProvider;

    @Autowired
    public PivotViewServices(PivotViewProvider<T> pivotViewProvider) {
        this.pivotViewProvider = pivotViewProvider;
    }

    @Operation(summary = "Returns a pivotView view")
    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(value = "", produces = MediaType.APPLICATION_XML_VALUE)
    public String getView(
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

        if (valueParameters.size() % 2 == 1) {
            throw new BadRequestException("Invalid number of parameters.");
        }

        final Pair<String, Object>[] pairs = new Pair[valueParameters.size() / 2];
        for (int i = 0; i < valueParameters.size(); i += 2) {
            pairs[i] = Pair.of(valueParameters.get(i), valueParameters.get(i + 1));
        }

        return pivotViewProvider.get(organizationId, tenantId, tag, group, elementId, startDate, endDate, lastDays, pairs);
    }


}
