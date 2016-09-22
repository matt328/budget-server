package org.matt.budget.rest;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.matt.budget.service.exception.ObjectNotFoundException;
import org.matt.budget.service.exception.ServiceException;

@Provider
public class ServiceExceptionMapper implements ExceptionMapper<ServiceException> {

  ResourceBundle bundle = ResourceBundle.getBundle("messages");

  private static final Map<Class<? extends ServiceException>, String> exceptionTypeMap;

  static {
    exceptionTypeMap = new HashMap<>();
    exceptionTypeMap.put(ObjectNotFoundException.class, "messages.objectNotFound");
  }

  @Override
  public Response toResponse(ServiceException exception) {

    String message = exceptionTypeMap.get(exception.getClass());

    Map<String, String> map = new HashMap<>();
    map.put("message", message);

    return Response.status(Response.Status.BAD_REQUEST)
                   .type(MediaType.APPLICATION_JSON)
                   .entity(map)
                   .build();
  }

}
