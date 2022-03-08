package com.biit.factmanager.test;

import com.biit.factmanager.persistence.entities.StringFact;
import com.biit.factmanager.persistence.repositories.FactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.Collection;

@SpringBootTest
@Test(groups = "factRepository")
@Rollback(false)
public class FindByFactsTests extends AbstractTransactionalTestNGSpringContextTests {

    @Autowired
    private FactRepository<StringFact> factRepository;

    @Test
    private void getFindBy() {
        Collection<StringFact> fact1 =  factRepository.findBy("1","1","1","1","1",
                LocalDateTime.now().minusDays(30), LocalDateTime.now().plusDays(1));

        Collection<StringFact> fact2 =  factRepository.findBy("2","2","2","2","2",
                LocalDateTime.now().minusDays(30), LocalDateTime.now().plusDays(1));

        Collection<StringFact> fact3 =  factRepository.findBy("3","3","3","3","3",
                LocalDateTime.now().minusDays(30), LocalDateTime.now().plusDays(1));

        Assert.assertEquals(fact1.size(),1);
        Assert.assertEquals(fact2.size(),1);
        Assert.assertEquals(fact3.size(),1);
    }
}
