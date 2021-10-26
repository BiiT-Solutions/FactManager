package com.biit.factmanager.persistence.configuration;


import com.biit.factmanager.logger.FactManagerLogger;
import com.biit.utils.configuration.ConfigurationReader;
import com.biit.utils.configuration.PropertiesSourceFile;
import com.biit.utils.configuration.SystemVariablePropertiesSourceFile;
import com.biit.utils.configuration.exceptions.PropertyNotFoundException;
import com.biit.utils.file.watcher.FileWatcher.FileModifiedListener;

import java.nio.file.Path;

public class FactManagerConfigurationReader extends ConfigurationReader {
    private static final String CONFIG_FILE = "settings.conf";
    private static final String SYSTEM_VARIABLE_CONFIG = "FACT_MANAGER_CONFIG";

    private static final String KEY_DESERIALIZER_CLASS_CONFIG = "key.deserializer";
    private static final String VALUE_DESERIALIZER_CLASS_CONFIG = "value.deserializer";
    private static final String BOOTSTRAP_SERVERS_CONFIG = "bootstrap.servers";
    private static final String KEY_SERIALIZER_CLASS_CONFIG = "key.serializer";
    private static final String VALUE_SERIALIZER_CLASS_CONFIG = "value.serializer";
    private static final String ID_DATABASE_ENCRYPTION_KEY = "database.encryption.key";

    // Default
    private static final String DEFAULT_KEY_DESERIALIZER_CLASS_CONFIG = "org.apache.kafka.common.serialization.StringDeserializer";
    private static final String DEFAULT_VALUE_DESERIALIZER_CLASS_CONFIG = "org.apache.kafka.common.serialization.StringDeserializer";
    private static final String DEFAULT_BOOTSTRAP_SERVERS_CONFIG = "kafka:9092";
    private static final String DEFAULT_KEY_SERIALIZER_CLASS_CONFIG = "org.apache.kafka.common.serialization.StringSerializer";
    private static final String DEFAULT_VALUE_SERIALIZER_CLASS_CONFIG = "org.apache.kafka.common.serialization.StringSerializer";


    private static volatile FactManagerConfigurationReader instance;

    private FactManagerConfigurationReader() {
        super();

        addProperty(KEY_DESERIALIZER_CLASS_CONFIG, DEFAULT_KEY_DESERIALIZER_CLASS_CONFIG);
        addProperty(VALUE_DESERIALIZER_CLASS_CONFIG, DEFAULT_VALUE_DESERIALIZER_CLASS_CONFIG);
        addProperty(BOOTSTRAP_SERVERS_CONFIG, DEFAULT_BOOTSTRAP_SERVERS_CONFIG);
        addProperty(KEY_SERIALIZER_CLASS_CONFIG, DEFAULT_KEY_SERIALIZER_CLASS_CONFIG);
        addProperty(VALUE_SERIALIZER_CLASS_CONFIG, DEFAULT_VALUE_SERIALIZER_CLASS_CONFIG);

        final PropertiesSourceFile sourceFile = new PropertiesSourceFile(CONFIG_FILE);
        sourceFile.addFileModifiedListeners(new FileModifiedListener() {

            @Override
            public void changeDetected(Path pathToFile) {
                FactManagerLogger.info(this.getClass().getName(), "WAR settings file '" + pathToFile + "' change detected.");
                readConfigurations();
            }
        });
        addPropertiesSource(sourceFile);
        final SystemVariablePropertiesSourceFile systemSourceFile = new SystemVariablePropertiesSourceFile(
                SYSTEM_VARIABLE_CONFIG, CONFIG_FILE);
        systemSourceFile.addFileModifiedListeners(new FileModifiedListener() {

            @Override
            public void changeDetected(Path pathToFile) {
                FactManagerLogger.info(this.getClass().getName(),
                        "System variable settings file '" + pathToFile + "' change detected.");
                readConfigurations();
            }
        });
        addPropertiesSource(systemSourceFile);

        readConfigurations();
    }


    public static FactManagerConfigurationReader getInstance() {
        if (instance == null) {
            synchronized (FactManagerConfigurationReader.class) {
                if (instance == null) {
                    instance = new FactManagerConfigurationReader();
                }
            }
        }
        return instance;
    }

    private String getPropertyLogException(String propertyId) {
        try {
            return getProperty(propertyId);
        } catch (PropertyNotFoundException e) {
            FactManagerLogger.errorMessage(this.getClass().getName(), e);
            return null;
        }
    }

    public String getKeyDeserializerClassConfig() {
        return getPropertyLogException(KEY_DESERIALIZER_CLASS_CONFIG);
    }

    public String getValueDeserializerClassConfig() {
        return getPropertyLogException(VALUE_DESERIALIZER_CLASS_CONFIG);
    }

    public String getBootstrapServersConfig() {
        return getPropertyLogException(BOOTSTRAP_SERVERS_CONFIG);
    }

    public String getKeySerializerClassConfig() {
        return getPropertyLogException(KEY_SERIALIZER_CLASS_CONFIG);
    }

    public String getValueSerializerClassConfig() {
        return getPropertyLogException(VALUE_SERIALIZER_CLASS_CONFIG);
    }

    public String getDatabaseEncryptionKey() {
        return getPropertyLogException(ID_DATABASE_ENCRYPTION_KEY);
    }


}
