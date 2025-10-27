package com.biit.factmanager.core.providers.exceptions;

/*-
 * #%L
 * FactManager (core)
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

import com.biit.factmanager.logger.ExceptionType;
import com.biit.factmanager.logger.LoggedException;
import org.springframework.http.HttpStatus;

import java.io.Serial;

public class InvalidParameterException extends LoggedException {
    @Serial
    private static final long serialVersionUID = -2510787123562846634L;

    public InvalidParameterException(Class<?> clazz, String message, ExceptionType type) {
        super(clazz, message, type, HttpStatus.NOT_FOUND);
    }

    public InvalidParameterException(Class<?> clazz, String message) {
        super(clazz, message, ExceptionType.WARNING, HttpStatus.NOT_FOUND);
    }

    public InvalidParameterException(Class<?> clazz) {
        this(clazz, "Comment not found");
    }

    public InvalidParameterException(Class<?> clazz, Throwable e) {
        super(clazz, e);
    }
}
