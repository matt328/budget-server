package org.matt.budget.service.exception;

public class ObjectNotFoundException extends ServiceException {

  private static final long serialVersionUID = 1L;

  public ObjectNotFoundException(String message) {
    super(message);
  }

  public ObjectNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

}
