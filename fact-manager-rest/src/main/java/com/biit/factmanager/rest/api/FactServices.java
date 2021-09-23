package com.biit.factmanager.rest.api;

import com.biit.factmanager.core.providers.FormrunnerFactProvider;
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
import java.util.Collection;
import java.util.List;

@RequestMapping(value = "/facts")
@RestController
public class FactServices {

	private final FormrunnerFactProvider formrunnerFactProvider;

	@Autowired
	public FactServices(FormrunnerFactProvider formrunnerFactProvider) {
		this.formrunnerFactProvider = formrunnerFactProvider;
	}

	@ApiOperation(value = "Get all facts", notes = "Id requires a level to be set. Parameters:")
	@ResponseStatus(value = HttpStatus.OK)
	@GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
	public Collection<FormrunnerFact> getFacts(
			@ApiParam(value = "Id", required = false) @RequestParam(value = "id", required = false) Long id,
			@ApiParam(value = "ExaminationName", required = false) @RequestParam(value = "examinationName", required = false) String examinationName,
			@ApiParam(value = "Level: [PATIENT, COMPANY, ORGANIZATION]", required = false) @RequestParam(value = "level", required = false) Level level,
			HttpServletRequest httpRequest
	) {
		FactManagerLogger.info(this.getClass().getName(), "Get all facts");
		return formrunnerFactProvider.getFiltered(level, id, examinationName);
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
	public List<FormrunnerFact> addFactList(@ApiParam(value = "Notification Request", required = true) @RequestBody List<FormrunnerFact> facts,
						HttpServletRequest httpRequest) {
		/*FactManagerLogger.info(this.getClass().getName(), "Saving a list of facts");
		final List<Fact> savedFacts = new ArrayList<>();
		for (final Fact fact: facts) {
			final Fact savedFact = factProvider.add(fact);
			savedFacts.add(savedFact);
			FactManagerLogger.debug(this.getClass().getName(), "Save fact " + fact.toString());
		}*/
		// return savedFacts;
		return formrunnerFactProvider.save(facts);
	}

	@ApiOperation(value = "Deletes a fact", notes = "Parameters:\n"
			+ "fact (required): Fact object to be removed.")
	@ResponseStatus(value = HttpStatus.OK)
	@DeleteMapping(value = "")
	public void deleteFact(@ApiParam(value = "Fact entity", required = true) @RequestBody FormrunnerFact fact,
						HttpServletRequest httpRequest) {
		FactManagerLogger.info(this.getClass().getName(), "Remove fact");
		formrunnerFactProvider.delete(fact);
	}


}
