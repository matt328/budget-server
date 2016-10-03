package org.matt.budget.service;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.matt.budget.models.Token;
import org.matt.budget.models.User;

@Stateless
public class UsersService extends RepositoryService<User> {

  private static final long serialVersionUID = 1L;

  @Inject
  TokenService tokenService;

  public Token addTokenToUser(Token token, String userId) {
    Map<String, Object> params = new HashMap<>();
    params.put(User.COL_USER_ID, userId);

    User user = findSingleWithNamedQuery(User.FIND_BY_USER_ID, params);

    token.setUser(user);

    tokenService.insert(token);

    user.getTokens().add(token);

    repo().update(user);

    return token;
  }

}
