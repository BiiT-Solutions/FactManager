package com.biit.factmanager.rest;

import com.biit.factmanager.rest.utils.ArtGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.time.Period;
import java.util.Collections;

@SpringBootApplication
@ComponentScan({"com.biit.factmanager"})
@ConfigurationPropertiesScan({"com.biit.factmanager.rest"})
@EnableJpaRepositories({"com.biit.factmanager.persistence.repositories"})
@EntityScan({"com.biit.factmanager.persistence.entities"})
@EnableSwagger2
public class FactManagerServicesServer {
	private static final String SWAGGER_TITLE = "FactManager";
	private static final String SWAGGER_REST_LOCATION = "com.biit.factmanager.rest";
	private static final Class[] IGNORED_CLASSES = {};

	public static void main(String[] args) {
		if (System.getenv().containsKey("developer")) {
			printWelcome();
		}
		SpringApplication.run(FactManagerServicesServer.class, args);
	}

	/*@Bean
	public Docket templateApi() {
		return new Docket(DocumentationType.SWAGGER_2)
				// OpenAPI doc cannot handle period
				.directModelSubstitute(Period.class, String.class).directModelSubstitute(CompleteCustomField.class, String.class).select()
				.apis(RequestHandlerSelectors.basePackage(SWAGGER_REST_LOCATION)).paths(PathSelectors.any()).build()
				.forCodeGeneration(true).apiInfo(getApiInfo()).ignoredParameterTypes(IGNORED_CLASSES);
	}*/

	private ApiInfo getApiInfo() {
		return new ApiInfo(SWAGGER_TITLE, SWAGGER_TITLE, "1.0", "",
				new Contact("Support", "https://www.biit-solutions.com/", "support@biit-solutions.com"),
				"BiiT Solutions Private Use Only License", "https://www.biit-solutions.com", Collections.emptyList());
	}

	@Bean(name = DispatcherServletAutoConfiguration.DEFAULT_DISPATCHER_SERVLET_BEAN_NAME)
	public DispatcherServlet dispatcherServlet() {
		return new LoggableDispatcherServlet();
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurerAdapter() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**").allowedMethods("HEAD", "GET", "PUT", "POST", "DELETE", "PATCH");
			}
		};
	}

	@Bean("threadPoolExecutor")
	public TaskExecutor getAsyncExecutor() {
		final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(20);
		executor.setMaxPoolSize(100);
		executor.setWaitForTasksToCompleteOnShutdown(true);
		executor.setThreadNamePrefix("Rest_Async-");
		return executor;
	}

	private static void printWelcome() {
		System.err.println("Welcome developer!");
		ArtGenerator.print(System.getProperty("user.name"), 10);
	}
}
