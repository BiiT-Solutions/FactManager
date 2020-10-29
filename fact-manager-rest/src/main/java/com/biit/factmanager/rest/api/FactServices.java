package com.biit.factmanager.rest.api;

import com.biit.factmanager.core.providers.FactProvider;
import com.biit.factmanager.logger.FactManagerLogger;
import com.biit.factmanager.persistence.entities.Fact;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RequestMapping(value = "/facts")
@RestController
public class FactServices {

	private final FactProvider factProvider;

	@Autowired
	public FactServices(FactProvider factProvider) {
		this.factProvider = factProvider;
	}

	@ApiOperation(value = "Get all facts", notes = "Parameters:")
	@ResponseStatus(value = HttpStatus.OK)
	@GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
	public Collection<Fact> getAllFacts(HttpServletRequest httpRequest) {
		FactManagerLogger.info(this.getClass().getName(), "Get all facts");
		return factProvider.getAll();
	}

	/*@ApiOperation(value = "Adds a new fact", notes = "Parameters:\n"
			+ "fact (required): Fact object to be added")
	@ResponseStatus(value = HttpStatus.CREATED)
	@PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
	public Fact addFact(@ApiParam(value = "Notification Request", required = true) @RequestBody Fact fact,
						HttpServletRequest httpRequest) {
		FactManagerLogger.info(this.getClass().getName(), "Add fact");
		return factProvider.add(fact);
	}*/

	@ApiOperation(value = "Save a list of facts", notes = "Parameters:\n"
			+ "facts (required): List of Fact objects to be added")
	@ResponseStatus(value = HttpStatus.CREATED)
	@PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Fact> addFactList(@ApiParam(value = "Notification Request", required = true) @RequestBody List<Fact> facts,
						HttpServletRequest httpRequest) {
		FactManagerLogger.info(this.getClass().getName(), "Save a list of facts");
		List<Fact> savedFacts = new ArrayList<>();
		for (Fact fact: facts) {
			Fact savedFact = factProvider.add(fact);
			savedFacts.add(savedFact);
		}
		return savedFacts;
	}

	@ApiOperation(value = "Deletes a fact", notes = "Parameters:\n"
			+ "fact (required): Fact object to be removed.")
	@ResponseStatus(value = HttpStatus.OK)
	@DeleteMapping(value = "")
	public void deleteFact(@ApiParam(value = "Notification Request", required = true) @RequestBody Fact fact,
						HttpServletRequest httpRequest) {
		FactManagerLogger.info(this.getClass().getName(), "Remove fact");
		factProvider.delete(fact);
	}


}
