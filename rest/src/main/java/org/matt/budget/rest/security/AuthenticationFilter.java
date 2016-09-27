package org.matt.budget.rest.security;

import java.io.IOException;

import javax.annotation.Priority;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.matt.budget.rest.common.security.AuthenticatedUser;
import org.matt.budget.rest.common.security.Secured;
import org.matt.budget.service.AuthService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

  @Inject
  AuthService authService;

  @Inject
  @AuthenticatedUser
  Event<String> userAuthenticatedEvent;

  @Override
  public void filter(ContainerRequestContext requestContext) throws IOException {

    String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

    if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
      log.debug("No Auth Header present");
      throw new NotAuthorizedException("Authorization header must be provided");
    }

    String token = authorizationHeader.substring("Bearer".length()).trim();

    if (!authService.isTokenValid(token)) {
      requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
    } else {
      userAuthenticatedEvent.fire(authService.extractUser(token));
    }
  }
}
