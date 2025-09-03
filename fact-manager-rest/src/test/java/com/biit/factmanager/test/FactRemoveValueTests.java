package com.biit.factmanager.test;

import com.biit.drools.form.DroolsSubmittedForm;
import com.biit.factmanager.core.providers.FactProvider;
import com.biit.factmanager.persistence.entities.LogFact;
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
import java.util.Arrays;
import java.util.List;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Test(groups = {"removeFactValue"})
public class FactRemoveValueTests extends AbstractTransactionalTestNGSpringContextTests {

    private static final String USER_NAME = "user";
    private static final String USER_PASSWORD = "password";

    private static final String TOPIC = "form";
    private static final String APPLICATION = "XFORMS";
    private static final String TYPE = "DroolsResultForm";
    private static final String ORGANIZATION = "NHM";
    private static final String SUBJECT = "CREATED";
    private static final String UNIT_1 = "UNIT_1";
    private static final String UNIT_2 = "UNIT_2";

    private static final String FORM_AS_JSON = "The 5 Frustrations on Teamworking 1 - Drools.json";
    private static final String FORM_AS_JSON_2 = "The 5 Frustrations on Teamworking 2 - Drools.json";
    private static final String FORM_AS_JSON_3 = "The 5 Frustrations on Teamworking 3 - Drools.json";

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

    private <T> String toJson(T object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }

    private <T> T fromJson(String payload, Class<T> clazz) throws IOException {
        return objectMapper.readValue(payload, clazz);
    }

    @BeforeClass
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
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
    public void getFactWithValue() throws Exception {
        MvcResult createResult = this.mockMvc
                .perform(get("/facts")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        final List<LogFact> facts = Arrays.asList(fromJson(createResult.getResponse().getContentAsString(), LogFact[].class));
        Assert.assertEquals(facts.size(), 3);
        Assert.assertNotNull(facts.get(0).getValue());
        Assert.assertFalse(facts.get(0).getValue().isEmpty());
    }

    @Test(dependsOnMethods = "setAuthentication")
    public void getFactWithoutValue() throws Exception {
        MvcResult createResult = this.mockMvc
                .perform(get("/facts?excludeValue=true")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        final List<LogFact> facts = Arrays.asList(fromJson(createResult.getResponse().getContentAsString(), LogFact[].class));
        Assert.assertEquals(facts.size(), 3);
        Assert.assertTrue(facts.get(0).getValue().isEmpty());
    }

    @AfterClass
    public void cleanDatabase() {
        factProvider.deleteAll();
    }
}
