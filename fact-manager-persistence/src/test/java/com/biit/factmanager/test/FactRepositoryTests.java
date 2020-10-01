package com.biit.factmanager.test;

import com.biit.factmanager.persistence.entities.Fact;
import com.biit.factmanager.persistence.repositories.FactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

@SpringBootTest
@Test(groups = "factRepository")
@Rollback(true)
public class FactRepositoryTests extends AbstractTransactionalTestNGSpringContextTests {

	@Autowired
	private FactRepository factRepository;

	private Fact fact = null;

	@Test
	private void getAllAtTheBeginning() {
		Assert.assertEquals(factRepository.count(), 0);
	}

	@Test(dependsOnMethods = "getAllAtTheBeginning")
	private void addFact() {
		fact =  factRepository.save(new Fact());
		Assert.assertEquals(factRepository.count(), 1);
	}

	@Test(dependsOnMethods = "addFact")
	private void deleteFact() {
		factRepository.delete(fact);
		Assert.assertEquals(factRepository.count(), 0);
	}
}
