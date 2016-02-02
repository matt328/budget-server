package org.matt.budget.rest;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.PersistenceException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class PersistenceExceptionMapper implements ExceptionMapper<PersistenceException> {

  /*
   * This should be a ServiceExceptionMapper
   *
   * Maybe make ServiceException a base class, and this mapper will map its subclasses
   * to response codes. - The service layer would have to always catch PersistenceExceptions
   * and depending on the curcumstance and specific PersistenceException, throw the appropriate
   * specific service exception.  Then the service layer is free to throw ServiceExceptions
   * if it's checks fail, and also catch PE's and throw SE's based on them.  That way users
   * of Service layer don't know anything about persistence and service doesnt' know anything
   * about jax-rs.  This mapper will take in a specific ServiceException, and map that to
   * a) a status code
   * b) a developer message
   * c) a user message.
   * Read messages from a properties file.
   *
   */

	@Override
	public Response toResponse(PersistenceException exception) {
		Map<String, String> map = new HashMap<>();
		map.put("message", exception.getMessage());

		return Response	.status(Response.Status.BAD_REQUEST)
										.type(MediaType.APPLICATION_JSON)
										.entity(map)
										.build();
	}

}
