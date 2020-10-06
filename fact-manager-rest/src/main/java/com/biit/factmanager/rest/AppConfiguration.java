package com.biit.factmanager.rest;

import com.azure.data.appconfiguration.ConfigurationClient;
import com.azure.data.appconfiguration.ConfigurationClientBuilder;
import com.azure.data.appconfiguration.models.SettingSelector;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;

import java.util.Properties;

@ConfigurationProperties(prefix = "azure")
public class AppConfiguration implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {
    private ConfigurationClient configurationClient = null;
    private final String appConfigurationConn = System.getenv("AppConfigConnection");
    // private final boolean developerMode = System.getenv().containsKey("developer");
    private static final boolean developerMode = true;
    private ConfigurationClient getConfigurationClient() {
        if (configurationClient == null) {
            configurationClient = new ConfigurationClientBuilder()
                    .connectionString(appConfigurationConn)
                    .buildClient();
        }
        return configurationClient;
    }

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        if (developerMode) {
            return;
        }
        final Properties properties = new Properties();
        final ConfigurableEnvironment environment = event.getEnvironment();
        final SettingSelector settingSelector = new SettingSelector().setKeyFilter("");
        final Properties azureProperties = new Properties();
        getConfigurationClient().listConfigurationSettings(settingSelector).forEach(setting -> {
            azureProperties.put(setting.getKey(), setting.getValue());
        });
        properties.put("spring.datasource.url", "jdbc:postgresql://" + azureProperties.get("open_project_config:url"));
        properties.put("spring.datasource.username", azureProperties.get("open_project_config:user"));
        properties.put("spring.datasource.password", azureProperties.get("open_project_config:password"));
        properties.put("azure.connection.string", "DefaultEndpointsProtocol=https;AccountName=" + azureProperties.get("cdn.blob.name") +
                ";AccountKey=" + azureProperties.get("cdn.blob.key"));
        properties.put("server.port", azureProperties.get("odas.server.port"));
        properties.put("server.servlet.context-path", azureProperties.get("odas.server.servlet.context-path"));
        properties.put("environment", azureProperties.get("env"));
        properties.put("spring.jpa.hibernate.ddl-auto", azureProperties.get("odas.spring.jpa.hibernate.ddl-auto"));
        properties.put("config.openproject.api.key", azureProperties.get("actionbook.config.openproject.api.key"));
        properties.put("config.openproject.api.server", azureProperties.get("actionbook.config.openproject.api.server"));
        properties.put("config.openproject.api.path", azureProperties.get("actionbook.config.openproject.api.path"));
        properties.put("config.odas.server", azureProperties.get("envbaseurl") + "odas");
        properties.put("server.max-http-header-size", 10000000);

        environment.getPropertySources().addFirst(new PropertiesPropertySource("application.properties", properties));
    }

}
