package com.biit.factmanager.client.fact;

public class ValueDTO {
    private String string;

    public ValueDTO() {

    }

    public ValueDTO(String value) {
        setString(value);
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }
}