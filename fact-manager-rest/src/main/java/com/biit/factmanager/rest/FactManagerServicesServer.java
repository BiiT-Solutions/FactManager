package com.biit.factmanager.rest;


import com.biit.factmanager.logger.FactManagerLogger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication
@ComponentScan({"com.biit.factmanager", "com.biit.server", "com.biit.messagebird.client"})
@ConfigurationPropertiesScan({"com.biit.factmanager.rest", "com.biit.factmanager.persistence.configuration",  "com.biit.server.security.userguard"})
@EntityScan({"com.biit.factmanager.persistence.entities"})
public class FactManagerServicesServer {

	public static void main(String[] args) {
		SpringApplication.run(FactManagerServicesServer.class, args);
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

	@Bean
	public ApplicationListener<ContextRefreshedEvent> startupLoggingListener() {
		return event -> FactManagerLogger.info(FactManagerServicesServer.class, "### Server started ###");
	}
}
