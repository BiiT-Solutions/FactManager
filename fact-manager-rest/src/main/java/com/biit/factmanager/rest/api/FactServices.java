package com.biit.factmanager.rest.api;

import com.biit.factmanager.core.providers.FormRunnerFactProvider;
import com.biit.factmanager.logger.FactManagerLogger;
import com.biit.factmanager.persistence.entities.Fact;
import com.biit.factmanager.persistence.entities.FormRunnerFact;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;

@RequestMapping(value = "/facts")
@RestController
public class FactServices {

    private final FormRunnerFactProvider formrunnerFactProvider;

    @Autowired
    public FactServices(FormRunnerFactProvider formrunnerFactProvider) {
        this.formrunnerFactProvider = formrunnerFactProvider;
    }

    @ApiOperation(value = "Get all facts", notes = "Id requires a level to be set. Parameters:\n"
            + "Group: grouping option of the event\n"
            + "ElementId: Id of the event\n")
    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<FormRunnerFact> getFacts(
            @ApiParam(value = "Group", required = false) @RequestParam(value = "group", required = false) String group,
            @ApiParam(value = "ElementId", required = false) @RequestParam(value = "elementId", required = false) String elementId,
            HttpServletRequest httpRequest) {
        FactManagerLogger.info(this.getClass().getName(), "Get all facts");
        return formrunnerFactProvider.getFiltered(group, elementId);
    }

    @ApiOperation(value = "Adds a new fact", notes = "Parameters:\n"
            + "fact (required): Fact object to be added")
    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Fact addFact(@ApiParam(value = "Notification Request", required = true) @RequestBody FormRunnerFact fact,
                        HttpServletRequest httpRequest) {
        FactManagerLogger.info(this.getClass().getName(), "Add fact");
        return formrunnerFactProvider.add(fact);
    }

    @ApiOperation(value = "Save a list of facts", notes = "Parameters:\n"
            + "facts (required): List of Fact objects to be added")
    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping(value = "/collection", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<FormRunnerFact> addFactList(@ApiParam(value = "Notification Request", required = true) @RequestBody List<FormRunnerFact> facts,
                                            HttpServletRequest httpRequest) {
        FactManagerLogger.debug(this.getClass().getName(), "Saving a list of facts '{}'.", facts);
        return formrunnerFactProvider.save(facts);
    }

    @ApiOperation(value = "Deletes a fact", notes = "Parameters:\n"
            + "fact (required): Fact object to be removed.")
    @ResponseStatus(value = HttpStatus.OK)
    @DeleteMapping(value = "")
    public void deleteFact(@ApiParam(value = "Fact entity", required = true) @RequestBody FormRunnerFact fact,
                           HttpServletRequest httpRequest) {
        FactManagerLogger.info(this.getClass().getName(), "Remove fact");
        formrunnerFactProvider.delete(fact);
    }


}
