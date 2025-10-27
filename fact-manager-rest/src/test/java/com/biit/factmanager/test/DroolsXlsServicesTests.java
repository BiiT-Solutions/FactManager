package com.biit.factmanager.test;

/*-
 * #%L
 * FactManager (Rest)
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

import com.biit.drools.form.DroolsSubmittedForm;
import com.biit.factmanager.core.providers.FactProvider;
import com.biit.factmanager.persistence.entities.LogFact;
import com.biit.factmanager.rest.api.model.XmlSearch;
import com.biit.server.security.model.AuthRequest;
import com.biit.usermanager.client.providers.AuthenticatedUserProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Test(groups = {"xmlServices"})
public class DroolsXlsServicesTests extends AbstractTransactionalTestNGSpringContextTests {

    private static final String USER_NAME = "user";
    private static final String USER_PASSWORD = "password";

    private static final String TOPIC = "form";
    private static final String APPLICATION = "XFORMS";
    private static final String TYPE = "DroolsResultForm";
    private static final String ORGANIZATION = "NHM";
    private static final String SUBJECT = "CREATED";
    private static final String UNIT_1 = "UNIT_1";
    private static final String UNIT_2 = "UNIT_2";

    private static final String OUTPUT_FOLDER = System.getProperty("java.io.tmpdir") + File.separator + "XmlForms";
    private static final String FORM_AS_JSON = "The 5 Frustrations on Teamworking 1 - Drools.json";
    private static final String FORM_AS_JSON_2 = "The 5 Frustrations on Teamworking 2 - Drools.json";
    private static final String FORM_AS_JSON_3 = "The 5 Frustrations on Teamworking 3 - Drools.json";

    private static final String FORM_NAME = "The 5 Frustrations on Teamworking";

    @Autowired
    private FactProvider<LogFact> factProvider;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private AuthenticatedUserProvider authenticatedUserProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    private String jwtToken;


    private <T> String toJson(T object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }

    private LogFact getLogFact(String file, String unit) throws URISyntaxException, IOException {
        final String text = new String(Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource("SubmittedForms" + File.separator + file).toURI())));
        final DroolsSubmittedForm form = DroolsSubmittedForm.getFromJson(text);
        final LogFact logFact = new LogFact();
        logFact.setValue(text);
        logFact.setElementName(form.getTag());
        logFact.setGroup(TOPIC);
        logFact.setApplication(APPLICATION);
        logFact.setFactType(TYPE);
        logFact.setOrganization(ORGANIZATION);
        logFact.setSubject(SUBJECT);
        logFact.setUnit(unit);
        return logFact;
    }

    @BeforeClass
    public void prepareFolder() throws IOException {
        Files.createDirectories(Paths.get(OUTPUT_FOLDER));
    }

    @BeforeClass
    public void addFacts() throws URISyntaxException, IOException {
        List<LogFact> facts = new ArrayList<>();
        facts.add(getLogFact(FORM_AS_JSON, UNIT_1));
        facts.add(getLogFact(FORM_AS_JSON_2, UNIT_1));
        facts.add(getLogFact(FORM_AS_JSON_3, UNIT_2));
        factProvider.saveAll(facts);
    }

    @BeforeClass
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @BeforeClass(dependsOnMethods = "setUp")
    public void addUser() {
        //Create the admin user
        authenticatedUserProvider.createUser(USER_NAME, USER_NAME, USER_PASSWORD);
    }

    @Test()
    public void checkAuthentication() {
        //Check the admin user
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(USER_NAME, USER_PASSWORD));
    }

    @Test(dependsOnMethods = "checkAuthentication")
    public void setAuthentication() throws Exception {
        AuthRequest request = new AuthRequest();
        request.setUsername(USER_NAME);
        request.setPassword(USER_PASSWORD);

        MvcResult createResult = this.mockMvc
                .perform(post("/auth/public/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request))
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.header().exists(HttpHeaders.AUTHORIZATION))
                .andReturn();

        jwtToken = createResult.getResponse().getHeader(HttpHeaders.AUTHORIZATION);
        Assert.assertNotNull(jwtToken);
    }


    @Test(dependsOnMethods = "setAuthentication")
    public void getXlsFile() throws Exception {
        MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("elementName", FORM_NAME);
        requestParams.add("lastDays", "1");

        final MvcResult createResult = mockMvc.perform(get("/facts/drools-forms/xls")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                        .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_OCTET_STREAM)
                        .params(requestParams)
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();


        //Store the file for comparison.
        final File xlsFile = new File(OUTPUT_FOLDER + File.separator + "getDroolsXlsFile.xls");
        Files.write(xlsFile.toPath(), createResult.getResponse().getContentAsByteArray());
    }


    @Test(dependsOnMethods = "setAuthentication")
    public void getMultipleXlsFile() throws Exception {
        final List<XmlSearch> searches = new ArrayList<>();

        final XmlSearch xmlSearch1 = new XmlSearch();
        xmlSearch1.setElementName(FORM_NAME);
        xmlSearch1.setLastDays(1);
        xmlSearch1.setLatestByUser(true);
        xmlSearch1.setUnit(UNIT_1);
        searches.add(xmlSearch1);

        final XmlSearch xmlSearch2 = new XmlSearch();
        xmlSearch2.setElementName(FORM_NAME);
        xmlSearch2.setLastDays(1);
        xmlSearch2.setLatestByUser(true);
        xmlSearch2.setUnit(UNIT_2);
        searches.add(xmlSearch2);


        final MvcResult createResult = mockMvc.perform(post("/facts/drools-forms/xls")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                        .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_OCTET_STREAM)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(searches))
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();


        //Store the file for comparison.
        final File xlsFile = new File(OUTPUT_FOLDER + File.separator + "DroolsMultipleXlsFile.xls");
        Files.write(xlsFile.toPath(), createResult.getResponse().getContentAsByteArray());
    }


    @AfterClass
    public void cleanDatabase() {
        factProvider.deleteAll();
    }

    private boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }

    @AfterClass
    public void removeFolder() {
        Assert.assertTrue(deleteDirectory(new File(OUTPUT_FOLDER)));
    }
}
