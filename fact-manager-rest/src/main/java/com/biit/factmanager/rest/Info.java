package com.biit.factmanager.rest;

import com.biit.factmanager.logger.FactManagerLogger;
import io.swagger.annotations.ApiOperation;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.naming.AuthenticationException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@RestController
public class Info {

    private Environment environment;
    private ApplicationArguments applicationArguments;

    @Autowired
    public Info(Environment environment, ApplicationArguments applicationArguments) {
        this.environment = environment;
        this.applicationArguments = applicationArguments;
    }

    @ApiIgnore
    @ApiOperation(value = "Redirects root address to API web site.")
    @GetMapping(value = "/")
    @ResponseStatus(HttpStatus.OK)
    public void root(HttpServletResponse response, HttpServletRequest httpRequest) throws IOException {
        response.sendRedirect("./swagger-ui.html");
    }

    @ApiOperation(value = "Configuration info")
    @PostMapping(value = "/info", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.TEXT_PLAIN_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Map<String, String> info(HttpServletRequest request, @RequestBody String hash)
            throws AuthenticationException {
        final String infoPassword = environment.getProperty("config.api.info.password");
        if (infoPassword == null) {
            throw new AuthenticationException("Password not set. Please check your settings file");
        }

        if (!hash.equalsIgnoreCase(infoPassword)) {
            FactManagerLogger.warning(getClass().getName(),
                    "IP :" + request.getRemoteAddr() + "  tried to get Info with an invalid password");
            throw new AuthenticationException("Incorrect Password");
        }
        final HashMap<String, String> info = new HashMap<>();
        // For testing purpose.
        info.put("Compilation Token", "X9HIJ4-1YP");
        info.put("spring.jpa.hibernate.ddl-auto", environment.getProperty("spring.jpa.hibernate.ddl-auto"));
        info.put("spring.datasource.url", environment.getProperty("spring.datasource.url"));
        info.put("spring.datasource.username", environment.getProperty("spring.datasource.username"));
        info.put("spring.datasource.password", environment.getProperty("spring.datasource.password"));
        info.put("server.port", environment.getProperty("server.port"));
        info.put("server.servlet.context-path", environment.getProperty("server.servlet.context-path"));
        info.put("environment", environment.getProperty("environment"));
        info.put("path", Info.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        for (final String arg : applicationArguments.getOptionNames()) {
            info.put("application.args." + arg, applicationArguments.getOptionValues(arg).get(0));
        }
        return info;
    }
}
