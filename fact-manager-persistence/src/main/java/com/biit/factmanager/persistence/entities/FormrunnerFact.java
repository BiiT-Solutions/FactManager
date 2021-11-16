package com.biit.factmanager.persistence.entities;


import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@DiscriminatorValue("FormRunnerFact")
public class FormrunnerFact extends Fact<FormRunnerValue> {

    @Transient
    private final FormRunnerValue formRunnerValue;

    public FormrunnerFact() {
        formRunnerValue = new FormRunnerValue();
    }

    @Override
    public void setCreatedAt(LocalDateTime localDateTime) {
        super.setCreatedAt(localDateTime);
    }
}