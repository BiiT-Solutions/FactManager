package com.biit.factmanager.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "singleton")
public class FactUrlConstructor {

    @Value("${factmanager.server.url}")
    private String factServerUrl;

    public String getFactServerUrl() {
        return factServerUrl;
    }

    public String addFacts() {
        return "/collection";
    }

    public String findByParameters() {
        return "/facts";
    }

    public String updateBySession(String session) {
        return "/facts/update/session/" + session;
    }
}
