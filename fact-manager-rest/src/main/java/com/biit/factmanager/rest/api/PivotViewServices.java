package com.biit.factmanager.rest.api;

import com.biit.factmanager.core.providers.PivotViewProvider;
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
import java.util.List;

@RequestMapping(value = "/pivotView")
@RestController
public class PivotViewServices {

    private final PivotViewProvider pivotViewProvider;

    @Autowired
    public PivotViewServices(PivotViewProvider pivotViewProvider) {
        this.pivotViewProvider = pivotViewProvider;
    }

    @ApiOperation(value = "Get facts by params")
    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(value = "", produces = MediaType.APPLICATION_XML_VALUE)
    public String getFacts(
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
