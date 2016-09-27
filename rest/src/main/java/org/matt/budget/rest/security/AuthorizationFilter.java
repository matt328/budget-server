package org.matt.budget.rest.security;

import java.io.IOException;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.matt.budget.models.User;
import org.matt.budget.rest.common.security.AuthenticatedUser;
import org.matt.budget.rest.common.security.Role;
import org.matt.budget.rest.common.security.Secured;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Secured
@Provider
@Priority(Priorities.AUTHORIZATION)
public class AuthorizationFilter implements ContainerRequestFilter {

  @Inject
  @AuthenticatedUser
  User user;

  @Context
  private ResourceInfo resourceInfo;

  @Override
  public void filter(ContainerRequestContext requestContext) throws IOException {
    System.out.println("Hello");
    Class<?> resourceClass = resourceInfo.getResourceClass();
    List<Role> classRoles = extractRoles(resourceClass);

    Method resourceMethod = resourceInfo.getResourceMethod();
    List<Role> methodRoles = extractRoles(resourceMethod);

    if (methodRoles.isEmpty()) {
      if (!checkPermissions(classRoles)) {
        log.warn("User: {} tried to call {} but did not have sufficient roles", user.getUserId(), resourceClass.getName() + "." + resourceMethod.getName());
        requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).build());
      }
    } else {
      if (!checkPermissions(methodRoles)) {
        log.warn("User: {} tried to call {} but did not have sufficient roles", user.getUserId(), resourceClass.getName() + "." + resourceMethod.getName());
        requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).build());
      }
    }
  }

  private List<Role> extractRoles(AnnotatedElement annotatedElement) {
    if (annotatedElement == null) {
      return new ArrayList<Role>();
    } else {
      Secured secured = annotatedElement.getAnnotation(Secured.class);
      if (secured == null) {
        return new ArrayList<Role>();
      } else {
        Role[] allowedRoles = secured.value();
        return Arrays.asList(allowedRoles);
      }
    }
  }

  private boolean checkPermissions(List<Role> allowedRoles) {
    List<String> allowed = allowedRoles.stream()
                                       .map(Role::toString)
                                       .collect(Collectors.toList());

    List<String> currentRoles = user.getRoles().stream()
                                    .map(org.matt.budget.models.Role::getName)
                                    .collect(Collectors.toList());

    return currentRoles.containsAll(allowed);
  }
}
