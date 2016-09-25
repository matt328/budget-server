package org.matt.budget.rest.security;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.matt.budget.models.User;
import org.matt.budget.rest.common.security.AuthenticatedUser;
import org.matt.budget.service.UserService;

@RequestScoped
public class AuthenticatedUserProducer {

  @Produces
  @RequestScoped
  @AuthenticatedUser
  private User authenticatedUser;

  @Inject
  UserService userService;

  public void handleAuthenticationEvent(@Observes @AuthenticatedUser String username) {
    this.authenticatedUser = findUser(username);
  }

  private User findUser(String username) {
    Map<String, Object> params = new HashMap<>();
    params.put(User.COL_USER_ID, username);
    return userService.findSingleWithNamedQuery(User.FIND_BY_USER_ID, params);
  }
}
