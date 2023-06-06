package com.biit.factmanager;

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
@EnableJpaRepositories(entityManagerFactoryRef = "factmanagerSystemFactoryTest", transactionManagerRef = "factmanagerTransactionManagerTest", basePackages = {
        FactManagerDatabaseConfigurationTest.PACKAGE})
public class FactManagerDatabaseConfigurationTest {
    public static final String PACKAGE = "com.biit.factmanager.persistence";

    @Autowired
    private Environment environment;

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.factmanager.datasource")
    public DataSource factmanagerDataSourceTest() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "factmanagerSystemFactoryTest")
    @Primary
    public LocalContainerEntityManagerFactoryBean factmanagerSystemFactoryTest(EntityManagerFactoryBuilder builder,
                                                                               @Qualifier("factmanagerDataSourceTest") DataSource dataSource) {
        final HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", environment.getProperty("spring.factmanager.datasource.jpa.hibernate.ddl-auto"));
        properties.put("hibernate.dialect", environment.getProperty("spring.factmanager.datasource.hibernate.dialect"));
        return builder.dataSource(dataSource).properties(properties).packages(PACKAGE).build();
    }

    @Bean
    @Primary
    public PlatformTransactionManager factmanagerTransactionManagerTest(
            @Qualifier("factmanagerSystemFactoryTest") EntityManagerFactory factmanagerSystemFactoryTest) {
        return new JpaTransactionManager(factmanagerSystemFactoryTest);
    }

}
