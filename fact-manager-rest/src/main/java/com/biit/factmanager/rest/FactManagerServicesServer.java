package com.biit.factmanager.rest;


import com.biit.factmanager.core.providers.FactProvider;
import com.biit.factmanager.logger.FactManagerLogger;
import com.biit.factmanager.persistence.entities.BasicFact;
import com.biit.factmanager.persistence.repositories.FactRepository;
import org.springframework.beans.factory.annotation.Qualifier;
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

@SpringBootApplication
@ComponentScan({"com.biit.factmanager", "com.biit.server", "com.biit.messagebird.client", "com.biit.usermanager.client"})
@ConfigurationPropertiesScan({"com.biit.factmanager.rest", "com.biit.server.security.userguard"})
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

    @Bean
    @Qualifier("basicFactProvider")
    public FactProvider<BasicFact> getBasicFactProvider(FactRepository<BasicFact> factRepository) {
        return new FactProvider<>(BasicFact.class, factRepository);
    }
}
