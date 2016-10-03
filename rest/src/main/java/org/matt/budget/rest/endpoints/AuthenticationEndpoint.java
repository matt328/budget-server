package org.matt.budget.rest.endpoints;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.matt.budget.rest.aop.Timed;
import org.matt.budget.rest.common.AuthenticationResource;
import org.matt.budget.rest.common.Credentials;
import org.matt.budget.service.AuthService;

@RequestScoped
public class AuthenticationEndpoint implements AuthenticationResource {

  @Inject
  AuthService authService;

  @Override
  @Timed
  public Response authenticateUser(Credentials credentials) {
    if (authService.authenticate(credentials.getUserId(), credentials.getPassword())) {
      String token = authService.issueToken(credentials.getUserId(), "testDevice");
      if (token != null) {
        return Response.ok(token).build();
      } else {
        return Response.status(Response.Status.UNAUTHORIZED).build();
      }
    } else {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
  }

}
