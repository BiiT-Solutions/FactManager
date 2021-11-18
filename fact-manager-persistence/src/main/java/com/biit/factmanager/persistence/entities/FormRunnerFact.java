package com.biit.factmanager.persistence.entities;


import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;
import java.time.LocalDateTime;


@Entity
@DiscriminatorValue("FormRunnerFact")
public class FormRunnerFact extends Fact<FormRunnerValue> {

    @Transient
    private final FormRunnerValue formRunnerValue;

    public FormRunnerFact() {
        formRunnerValue = new FormRunnerValue();
    }

    @Override
    public void setCreatedAt(LocalDateTime localDateTime) {
        super.setCreatedAt(localDateTime);
    }


}