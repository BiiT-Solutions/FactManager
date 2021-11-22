package com.biit.factmanager.logger;

import org.springframework.http.HttpStatus;

public abstract class LoggedException extends RuntimeException {
	private static final long serialVersionUID = -2118048384077287599L;
	private HttpStatus status;

	public LoggedException(Class<?> clazz, String message, ExceptionType type, HttpStatus status) {
		super(message);
		this.status = status;
		final String className = clazz.getName();
		switch (type) {
			case INFO:
				FactManagerLogger.info(className, message);
				break;
			case WARNING:
				FactManagerLogger.warning(className, message);
				break;
			case SEVERE:
				FactManagerLogger.severe(className, message);
				break;
			default:
				FactManagerLogger.debug(className, message);
				break;
		}
	}

	public LoggedException(Class<?> clazz, Throwable e, HttpStatus status) {
		this(clazz, e);
		this.status = status;
	}

	public LoggedException(Class<?> clazz, Throwable e) {
		super(e);
		FactManagerLogger.errorMessage(clazz, e);
	}

	public HttpStatus getStatus() {
		return status;
	}
}
