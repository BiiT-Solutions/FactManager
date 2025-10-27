package com.biit.factmanager.rest;

/*-
 * #%L
 * FactManager (Rest Server)
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


import com.biit.factmanager.core.providers.FactProvider;
import com.biit.factmanager.logger.FactManagerLogger;
import com.biit.factmanager.persistence.entities.BasicFact;
import com.biit.factmanager.persistence.entities.FormrunnerFact;
import com.biit.factmanager.persistence.entities.FormrunnerQuestionFact;
import com.biit.factmanager.persistence.repositories.FactRepository;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.web.servlet.DispatcherServlet;

//Avoid Swagger redirecting https to http
@OpenAPIDefinition(servers = {@Server(url = "${server.servlet.context-path}", description = "Default Server URL")})
@SpringBootApplication
@ComponentScan({"com.biit.factmanager", "com.biit.server", "com.biit.messagebird.client", "com.biit.usermanager.client", "com.biit.kafka"})
@ConfigurationPropertiesScan({"com.biit.factmanager.rest", "com.biit.factmanager.persistence.configuration", "com.biit.server.security.userguard",
        "com.biit.kafka"})
@EntityScan({"com.biit.factmanager.persistence.entities"})
public class FactManagerServicesServer {

    public static void main(String[] args) {
        SpringApplication.run(FactManagerServicesServer.class, args);
    }


    @Bean(name = DispatcherServletAutoConfiguration.DEFAULT_DISPATCHER_SERVLET_BEAN_NAME)
    public DispatcherServlet dispatcherServlet() {
        return new LoggableDispatcherServlet();
    }

    @Bean
    public ApplicationListener<ContextRefreshedEvent> startupLoggingListener() {
        return event -> FactManagerLogger.info(FactManagerServicesServer.class, "### Server started ###");
    }

    //These beans are needed to populate the discriminator value of Fact JPA Table, as needs the entityClass to be defined.
    @Bean
    public FactProvider<BasicFact> getBasicFactProvider(FactRepository<BasicFact> factRepository) {
        return new FactProvider<>(BasicFact.class, factRepository);
    }


    @Bean
    public FactProvider<FormrunnerFact> getFormRunnerFactProvider(FactRepository<FormrunnerFact> factRepository) {
        return new FactProvider<>(FormrunnerFact.class, factRepository);
    }

    @Bean
    public FactProvider<FormrunnerQuestionFact> getFormRunnerQuestionFactProvider(FactRepository<FormrunnerQuestionFact> factRepository) {
        return new FactProvider<>(FormrunnerQuestionFact.class, factRepository);
    }
}
