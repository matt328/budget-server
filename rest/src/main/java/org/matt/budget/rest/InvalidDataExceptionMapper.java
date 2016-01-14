package org.matt.budget.rest;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.matt.budget.service.exception.InvalidDataException;

@Provider
public class InvalidDataExceptionMapper implements ExceptionMapper<InvalidDataException> {

	@Override
	public Response toResponse(InvalidDataException exception) {
		Map<String, String> map = new HashMap<>();
		map.put("message", exception.getMessage());

		return Response	.status(Response.Status.BAD_REQUEST)
										.type(MediaType.APPLICATION_JSON)
										.entity(map)
										.build();
	}

}
