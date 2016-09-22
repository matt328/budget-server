package org.matt.budget.service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.matt.budget.models.Token;
import org.matt.budget.models.User;
import org.matt.budget.service.encryption.BCryptEncryption;
import org.matt.budget.service.encryption.EncryptionService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Stateless
public class AuthService implements Serializable {

  private static final long serialVersionUID = 1L;

  @Inject
  UserService userService;

  @Inject
  @BCryptEncryption
  EncryptionService encryptionService;

  @Inject
  TokenService tokenService;
  
  public boolean authenticate(String userId, String password) {
    Map<String, Object> queryParameters = new HashMap<>();
    queryParameters.put(User.COL_USER_ID, userId);
    User user = userService.findSingleWithNamedQuery(User.FIND_BY_USER_ID, queryParameters);
    if (user == null) {
      log.warn("Attempted login with non-existant userId: {}", userId);
      return false;      
    }
    if (!encryptionService.checkPassword(password, user.getPasswordHash())) {
      log.warn("Attempted login with userId: {} and invalid password", userId);
      return false;
    }
    return true;
  }

  public Token issueToken(String userId) {
    Map<String, Object> queryParameters = new HashMap<>();
    queryParameters.put(User.COL_USER_ID, userId);
    User user = userService.findSingleWithNamedQuery(User.FIND_BY_USER_ID, queryParameters);
    return tokenService.grantToken(user);
  }

}
