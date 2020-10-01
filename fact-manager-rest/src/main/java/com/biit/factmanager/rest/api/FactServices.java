package com.biit.factmanager.rest.api;

import com.biit.factmanager.core.providers.FactProvider;
import com.biit.factmanager.persistence.entities.Fact;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

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
	@GetMapping(value = "")
	public Collection<Fact> getAllFacts(HttpServletRequest httpRequest) {
		return factProvider.getAll();
	}

	@ApiOperation(value = "Adds a new fact", notes = "Parameters:\n"
			+ "fact (required): Fact object to be added")
	@ResponseStatus(value = HttpStatus.OK)
	@DeleteMapping(value = "")
	public Fact addFact(@ApiParam(value = "Notification Request", required = true) @RequestBody Fact fact,
						HttpServletRequest httpRequest) {
		return factProvider.add(fact);
	}

	@ApiOperation(value = "Deletes a fact", notes = "Parameters:\n"
			+ "fact (required): Fact object to be removed.")
	@ResponseStatus(value = HttpStatus.OK)
	@PostMapping(value = "")
	public void deleteFact(@ApiParam(value = "Notification Request", required = true) @RequestBody Fact fact,
						HttpServletRequest httpRequest) {
		factProvider.delete(fact);
	}


}
