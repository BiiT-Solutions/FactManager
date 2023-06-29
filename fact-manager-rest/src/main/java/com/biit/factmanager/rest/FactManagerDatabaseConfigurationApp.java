package com.biit.factmanager.rest;

import com.biit.factmanager.core.providers.FactProvider;
import com.biit.factmanager.persistence.entities.BasicFact;
import com.biit.factmanager.persistence.entities.FormrunnerFact;
import com.biit.factmanager.persistence.entities.FormrunnerQuestionFact;
import com.biit.factmanager.persistence.repositories.FactRepository;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;

@Configuration
@EnableJpaRepositories(entityManagerFactoryRef = "factmanagerSystemFactoryApp", transactionManagerRef = "factmanagerTransactionManagerApp", basePackages = {
        FactManagerDatabaseConfigurationApp.PACKAGE})
public class FactManagerDatabaseConfigurationApp {
    public static final String PACKAGE = "com.biit.factmanager.persistence";

    @Autowired
    private Environment environment;

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.factmanager.datasource")
    public DataSource factmanagerDataSourceApp() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "factmanagerSystemFactoryApp")
    @Primary
    public LocalContainerEntityManagerFactoryBean factmanagerSystemFactoryApp(EntityManagerFactoryBuilder builder,
                                                                           @Qualifier("factmanagerDataSourceApp") DataSource dataSource) {
        final HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", environment.getProperty("spring.factmanager.datasource.jpa.hibernate.ddl-auto"));
        properties.put("hibernate.dialect", environment.getProperty("spring.factmanager.datasource.hibernate.dialect"));
        return builder.dataSource(dataSource).properties(properties).packages(PACKAGE).build();
    }

    @Bean
    @Primary
    public PlatformTransactionManager factmanagerTransactionManagerApp(
            @Qualifier("factmanagerSystemFactoryApp") EntityManagerFactory factmanagerSystemFactoryApp) {
        return new JpaTransactionManager(factmanagerSystemFactoryApp);
    }

    //These beans are needed to populate the discriminator value of Fact JPA Table, as needs the entityClass to be defined.
    @Bean
    @Qualifier("basicFactProvider")
    public FactProvider<BasicFact> getBasicFactProvider(FactRepository<BasicFact> factRepository) {
        return new FactProvider<>(BasicFact.class, factRepository);
    }


    @Bean
    @Qualifier("formRunnerFactProvider")
    public FactProvider<FormrunnerFact> getFormRunnerFactProvider(FactRepository<FormrunnerFact> factRepository) {
        return new FactProvider<>(FormrunnerFact.class, factRepository);
    }

    @Bean
    @Qualifier("formRunnerQuestionFactProvider")
    public FactProvider<FormrunnerQuestionFact> getFormRunnerQuestionFactProvider(FactRepository<FormrunnerQuestionFact> factRepository) {
        return new FactProvider<>(FormrunnerQuestionFact.class, factRepository);
    }

}
