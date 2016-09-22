package org.matt.budget.service;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.matt.budget.models.Token;
import org.matt.budget.models.User;

@Stateless
public class TokenService extends RepositoryService<Token> {

  private static final long serialVersionUID = 1L;

  @Inject
  UserService userService;

  public Token grantToken(User user) {
    int noOfDays = 7;
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(new Date());
    calendar.add(Calendar.DAY_OF_YEAR, noOfDays);
    Date expireDate = calendar.getTime();

    String tokenString = UUID.randomUUID().toString().replaceAll("-", "");
    
    Token t = Token.builder().token(tokenString)
                   .expireDate(expireDate)
                   .user(user)
                   .build();

    user.getTokens().add(t);

    userService.update(user);

    insert(t);

    return t;
  }

}
