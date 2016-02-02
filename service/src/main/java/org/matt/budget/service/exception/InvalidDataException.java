package org.matt.budget.service.exception;

import javax.persistence.PersistenceException;

public class InvalidDataException extends PersistenceException {

	private static final long serialVersionUID = 1L;

	public InvalidDataException(String message) {
		super(message);
	}
}
