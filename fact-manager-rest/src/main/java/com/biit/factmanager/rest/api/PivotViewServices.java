package com.biit.factmanager.rest.api;

import com.biit.factmanager.core.providers.FormrunnerFactProvider;
import com.biit.factmanager.core.providers.PivotViewProvider;
import com.biit.factmanager.logger.FactManagerLogger;
import com.biit.factmanager.persistence.entities.FormrunnerFact;
import com.biit.factmanager.persistence.enums.Level;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;

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

    @ApiOperation(value = "Get all facts", notes = "Id requires a level to be set. Parameters:")
    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(value = "", produces = MediaType.APPLICATION_XML_VALUE)
    public Collection<FormrunnerFact> getAllFacts(
            @ApiParam(value = "Id", required = false) @RequestParam(value = "id", required = false) Long id,
            @ApiParam(value = "ExaminationName", required = false) @RequestParam(value = "examinationName", required = false) String examinationName,
            @ApiParam(value = "Level: [PATIENT, COMPANY, ORGANIZATION]", required = false) @RequestParam(value = "level", required = false) Level level,
            HttpServletRequest httpRequest
    ) {
        FactManagerLogger.info(this.getClass().getName(), "Get all facts");
        return pivotViewProvider.getAll();
    }

    @ApiOperation(value = "Get facts between dates", notes = "requires two different dates")
    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(value = "", produces = MediaType.APPLICATION_XML_VALUE)
    public Collection<FormrunnerFact> getFactsBetweenDates8(
        @ApiParam(value = "startDate", required = true) @RequestParam(value = "startDate", required = true) LocalDateTime startDate,
        @ApiParam(value = "endDate", required = true) @RequestParam(value = "endDate", required = true) LocalDateTime endDate,
        HttpServletRequest httpServletRequest
    ) {
        return pivotViewProvider.getBetweenDates(startDate,endDate);
    }

    @ApiOperation(value = "Get facts between dates", notes = "requires two different dates")
    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(value = "", produces = MediaType.APPLICATION_XML_VALUE)
    public Collection<FormrunnerFact> getFactsOneYearOld(
            @ApiParam(value = "startDate", required = true) @RequestParam(value = "startDate", required = true) LocalDateTime date,
            HttpServletRequest httpServletRequest
    ) {
        return pivotViewProvider.getOneYearOld(date);
    }

    @ApiOperation(value = "Get facts between dates", notes = "requires two different dates")
    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(value = "", produces = MediaType.APPLICATION_XML_VALUE)
    public Collection<FormrunnerFact> getDayFacts(
            @ApiParam(value = "date", required = true) @RequestParam(value = "date", required = true) LocalDateTime date,
            HttpServletRequest httpServletRequest
    ) {
        return pivotViewProvider.getOneYearOld(date);
    }
}
