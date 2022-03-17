package com.biit.factmanager.rest;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {
    private static final String SWAGGER_TITLE = "FactManager";
    private static final String SWAGGER_DESCRIPTION = "Fact Manager Swagger";

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("fact-manager-public")
                .pathsToMatch("/**")
                .build();
    }

    @Bean
    public OpenAPI factManagerAPI() {
        return new OpenAPI()
                .info(new Info().title(SWAGGER_TITLE)
                        .description(SWAGGER_DESCRIPTION)
                        .version("v1.0.0"))
                .externalDocs(new ExternalDocumentation()
                        .description("Fact Manager Readme")
                        .url("https://git.biit-solutions.com/BiiT/FactManager"));
    }

}
