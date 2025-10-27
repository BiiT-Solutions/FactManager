package com.biit.factmanager.rest.api;

/*-
 * #%L
 * FactManager (Rest)
 * %%
 * Copyright (C) 2020 - 2025 BiiT Sourcing Solutions S.L.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

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
