package com.zeusas.dp.exhibit.utils;

public class ServiceException extends RuntimeException {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 2611061967444672628L;

	public ServiceException(String message) {
		super(message);
	}

	public ServiceException(Throwable cause) {
		super(cause);
	}

	public ServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	public ServiceException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
