package com.biit.factmanager.rest;

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

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.factmanager.datasource")
    public DataSource factmanagerDataSourceApp() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "factmanagerSystemFactoryApp")
    @Primary
    public LocalContainerEntityManagerFactoryBean factmanagerSystemFactoryApp(EntityManagerFactoryBuilder builder,
                                                                              @Autowired Environment environment,
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
