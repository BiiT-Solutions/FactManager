package com.biit.factmanager.rest.api;

import com.biit.factmanager.core.providers.PivotViewProvider;
import com.biit.factmanager.logger.FactManagerLogger;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@RequestMapping(value = "/pivotView")
@RestController
public class PivotViewServices {

    private final FactServices factServices;
    private final PivotViewProvider pivotViewProvider;

    @Autowired
    public PivotViewServices(FactServices factServices, PivotViewProvider pivotViewProvider) {
        this.factServices = factServices;
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
            @ApiParam(value = "parameters", required = false) @RequestParam(value = "parameters", required = false) Pair<String, Object>... valueParameters) {
        FactManagerLogger.info(this.getClass().getName(), "Get facts by params");
        return pivotViewProvider.get(organizationId, tenantId, tag, group, elementId, startDate, endDate, lastDays);
    }


}
