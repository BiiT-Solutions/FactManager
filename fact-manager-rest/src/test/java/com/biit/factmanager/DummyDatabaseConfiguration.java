package com.biit.factmanager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;

//@Configuration
//@EnableTransactionManagement
//@EnableJpaRepositories(entityManagerFactoryRef = "dummySystemFactory", transactionManagerRef = "dummyTransactionManager", basePackages = {
//        DummyDatabaseConfiguration.PACKAGE})
public class DummyDatabaseConfiguration {
    public static final String PACKAGE = "com.biit.dummy.persistence";

    @Autowired
    private Environment environment;

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.dummy.datasource")
    public DataSource dummyDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean dummySystemFactory(EntityManagerFactoryBuilder builder,
                                                                           @Qualifier("dummyDataSource") DataSource dataSource) {
        final HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", environment.getProperty("spring.dummy.datasource.jpa.hibernate.ddl-auto"));
        properties.put("hibernate.dialect", environment.getProperty("spring.dummy.datasource.hibernate.dialect"));
        return builder.dataSource(dataSource).properties(properties).packages(PACKAGE).build();
    }

    @Bean
    @Primary
    public PlatformTransactionManager dummyTransactionManager(
            @Qualifier("dummySystemFactory") EntityManagerFactory userEntityManagerFactory) {
        return new JpaTransactionManager(userEntityManagerFactory);
    }

}
