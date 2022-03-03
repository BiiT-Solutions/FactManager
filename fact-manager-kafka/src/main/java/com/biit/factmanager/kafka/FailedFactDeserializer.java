package com.biit.factmanager.kafka;

import com.biit.factmanager.logger.FactManagerLogger;
import com.biit.factmanager.persistence.entities.Fact;
import org.springframework.kafka.support.serializer.FailedDeserializationInfo;

import java.util.function.Function;

public class FailedFactDeserializer implements Function<FailedDeserializationInfo, Fact<?>> {

    @Override
    public Fact<?> apply(FailedDeserializationInfo info) {
        FactManagerLogger.severe(this.getClass().getName(), info.toString());
        return null;
    }

}
