package org.matt.budget.persistence;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class RepositoryException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public RepositoryException(String message) {
		super(message);
	}

}
