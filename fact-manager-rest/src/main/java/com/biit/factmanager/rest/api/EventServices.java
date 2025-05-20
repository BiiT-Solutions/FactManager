package com.biit.factmanager.rest.api;

import com.biit.factmanager.kafka.consumers.EventController;
import com.biit.factmanager.logger.FactManagerLogger;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(value = "/events")
@RestController
@ConditionalOnExpression("${spring.kafka.enabled:false}")
public class EventServices {

    private final EventController eventController;

    public EventServices(EventController eventController) {
        this.eventController = eventController;
    }

    @Operation(summary = "Relaunch a fact as an event.", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasAnyAuthority(@securityService.adminPrivilege)")
    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping(value = "/send/{id}")
    public void sendFactAsEvent(
            @Parameter(description = "Id from the fact", required = true) @PathVariable("id") Long id,
            Authentication authentication, HttpServletRequest httpRequest) {
        FactManagerLogger.info(this.getClass().getName(), "Sending fact with id '{}' as event by '{}'.", id, authentication.getName());
        eventController.resendFact(id);
    }

}
