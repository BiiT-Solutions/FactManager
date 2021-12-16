package com.biit.factmanager.kafka.events;

import com.biit.eventstructure.event.IKafkaStorable;
import com.biit.factmanager.persistence.entities.Fact;
import com.fasterxml.jackson.core.type.TypeReference;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

public class FactEvent<Value> extends Fact<Value> implements IKafkaStorable {
    private String eventId;

    public FactEvent(String eventId, LocalDateTime creationTime) {
        this.eventId = eventId;
        setCreatedAt(creationTime);
    }

    public FactEvent() {
        this(UUID.randomUUID().toString(), LocalDateTime.now());
    }

    public LocalDateTime getCreationTime() {
        return getCreatedAt();
    }

    public String getEventId() {
        return eventId;
    }

    @Override
    protected TypeReference<Value> getJsonParser() {
        return null;
    }

    @Override
    public String getPivotViewerTag() {
        return null;
    }

    @Override
    public String getPivotViewerValue() {
        return null;
    }

    @Override
    public Integer getPivotViewerItemImageIndex() {
        return null;
    }
}
