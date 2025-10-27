package com.biit.factmanager.client;

/*-
 * #%L
 * FactManager (Rest Client)
 * %%
 * Copyright (C) 2020 - 2025 BiiT Sourcing Solutions S.L.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.biit.factmanager.FactManagerServer;
import com.biit.factmanager.client.provider.ClientFactProvider;
import com.biit.factmanager.core.providers.FactProvider;
import com.biit.factmanager.dto.FactDTO;
import com.biit.factmanager.dto.ObjectMapperFactory;
import com.biit.factmanager.persistence.FormrunnerTestFact;
import com.biit.factmanager.persistence.FormrunnerTestValue;
import com.biit.factmanager.persistence.entities.CustomProperty;
import com.biit.rest.exceptions.UnprocessableEntityException;
import com.biit.server.client.SecurityClient;
import com.biit.usermanager.client.providers.AuthenticatedUserProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = FactManagerServer.class)
@Test(groups = {"clientTests"})
public class ClientTests extends AbstractTestNGSpringContextTests {
    private static final String USER_NAME = "admin";
    private static final String USER_UNIQUE_ID = "00000000AA";
    private static final String USER_PASSWORD = "my-password";
    private final static String JWT_SALT = "my-salt";


    private static final String TENANT_ID = "tenant";
    private static final String GROUP = "group";
    private static final String ELEMENT_ID = "elementId";
    private static final String SUBJECT = "tag";
    private static final String ORGANIZATION = "organization";
    private static final String VARIABLE = "variable";
    private static final String VALUE = "value";
    private static final String VARIABLE_2 = "variable2";
    private static final String VALUE_2 = "value2";

    private static final String CUSTOM_PROPERTY_KEY = "key";
    private static final String CUSTOM_PROPERTY_VALUE = "value";

    private static final String JSON = "[{\"id\":2,\"organization\":\"organization\",\"tenantId\":\"tenant\",\"processId\":null,\"tag\":\"tag\",\"group\":\"group\",\"value\":\"{\\\"casa\\\":\\\"variable2\\\",\\\"cosa\\\":\\\"value2\\\"}\",\"elementId\":\"elementId\",\"createdAt\":\"2023-02-08T17:40:48.186364\",\"entity\":\"pasa\",\"pivotViewerTag\":null,\"pivotViewerValue\":null,\"pivotViewerItemImageIndex\":null}]";

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private AuthenticatedUserProvider authenticatedUserProvider;

    @Autowired
    private FactProvider<FormrunnerTestFact> factProvider;

    @Autowired
    private ClientFactProvider clientFactProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeClass
    public void setAuthentication() {
        //Create the admin user
        authenticatedUserProvider.createUser(USER_NAME, USER_UNIQUE_ID, USER_PASSWORD);
    }

    @BeforeClass
    public void setFacts() {
        FormrunnerTestValue formrunnerTestValue = new FormrunnerTestValue();
        formrunnerTestValue.setVariable(VARIABLE);
        formrunnerTestValue.setValue(VALUE);


        FormrunnerTestFact formrunnerTestFact = new FormrunnerTestFact();
        formrunnerTestFact.setTenant(TENANT_ID);
        formrunnerTestFact.setGroup(GROUP);
        formrunnerTestFact.setElement(ELEMENT_ID);
        formrunnerTestFact.setSubject(SUBJECT);
        formrunnerTestFact.setOrganization(ORGANIZATION);
        formrunnerTestFact.setCreatedAt(LocalDateTime.now().minusMinutes(2));
        formrunnerTestFact.setEntity(formrunnerTestValue);

        factProvider.save(formrunnerTestFact);
    }

    @Test
    public void checkAuthentication() {
        //Check the admin user
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(USER_NAME, JWT_SALT + USER_PASSWORD));
    }

    @Test
    public void testMapper() throws JsonProcessingException {
        List<FactDTO> facts = objectMapper.readValue(JSON, new TypeReference<>() {
        });
        Assert.assertEquals(facts.size(), 1);
        Assert.assertTrue(facts.get(0).getValue().contains("variable2"));
    }

    @Test
    public void getFact() throws UnprocessableEntityException {
        List<FactDTO> facts = clientFactProvider.get(new HashMap<>());
        Assert.assertEquals(facts.size(), 1);
    }

    @Test(dependsOnMethods = "getFact")
    public void addFact() throws UnprocessableEntityException, JsonProcessingException {
        FormrunnerTestValue formrunnerTestValue = new FormrunnerTestValue();
        formrunnerTestValue.setVariable(VARIABLE_2);
        formrunnerTestValue.setValue(VALUE_2);


        FormrunnerTestFact formrunnerTestFact = new FormrunnerTestFact();
        formrunnerTestFact.setTenant(TENANT_ID);
        formrunnerTestFact.setGroup(GROUP);
        formrunnerTestFact.setElement(ELEMENT_ID);
        formrunnerTestFact.setSubject(SUBJECT);
        formrunnerTestFact.setOrganization(ORGANIZATION);
        formrunnerTestFact.setCreatedAt(LocalDateTime.now().minusMinutes(1));
        formrunnerTestFact.setEntity(formrunnerTestValue);
        formrunnerTestFact.getCustomProperties().add(new CustomProperty(formrunnerTestFact, CUSTOM_PROPERTY_KEY, CUSTOM_PROPERTY_VALUE));

        FormrunnerTestFact fact = factProvider.save(formrunnerTestFact);
        Assert.assertNotNull(fact);


        List<FactDTO> loadedFacts = clientFactProvider.get(new HashMap<>());
        loadedFacts.sort(Comparator.comparing(FactDTO::getCreatedAt));

        Assert.assertEquals(loadedFacts.size(), 2);
        Assert.assertEquals(loadedFacts.get(0).getTenant(), TENANT_ID);
        Assert.assertEquals(loadedFacts.get(0).getGroup(), GROUP);
        Assert.assertEquals(loadedFacts.get(0).getElement(), ELEMENT_ID);
        Assert.assertEquals(loadedFacts.get(0).getSubject(), SUBJECT);
        Assert.assertEquals(loadedFacts.get(0).getOrganization(), ORGANIZATION);

        FormrunnerTestValue value = getEntityObject(loadedFacts.get(1), FormrunnerTestValue.class);

        Assert.assertEquals(value.getVariable(), VARIABLE_2);
        Assert.assertEquals(value.getValue(), VALUE_2);
        Assert.assertEquals(loadedFacts.get(1).getCustomProperties().size(), 1);
        Assert.assertEquals(loadedFacts.get(1).getCustomProperties().iterator().next().getKey(), CUSTOM_PROPERTY_KEY);
        Assert.assertEquals(loadedFacts.get(1).getCustomProperties().iterator().next().getValue(), CUSTOM_PROPERTY_VALUE);
    }

    private <T> T getEntityObject(FactDTO factDTO, Class<T> factClass) throws JsonProcessingException {
        return ObjectMapperFactory.getObjectMapper().readValue(factDTO.getValue(), factClass);
    }
}
