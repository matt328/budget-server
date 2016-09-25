package org.matt.budget.service;

import java.io.Serializable;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.matt.budget.models.SigningKey;
import org.matt.budget.models.User;
import org.matt.budget.service.encryption.BCryptEncryption;
import org.matt.budget.service.encryption.EncryptionService;
import org.matt.budget.service.exception.ServiceException;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Stateless
public class AuthService implements Serializable {

  private static final long serialVersionUID = 1L;

  @Inject
  UserService userService;

  @Inject
  SigningKeyService signingKeyService;

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

  public void createNewSigningKey() throws ServiceException {

    try {
      KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
      kpg.initialize(2048);
      KeyPair keyPair = kpg.generateKeyPair();

      PublicKey publicKey = keyPair.getPublic();
      PrivateKey privateKey = keyPair.getPrivate();

      SigningKey signingKey = new SigningKey();
      signingKey.setPrivateKey(privateKey);
      signingKey.setPublicKey(publicKey);

      signingKeyService.repo().save(signingKey);

    } catch (NoSuchAlgorithmException e) {
      log.warn("Algorithm RSA somehow is not supported", e);
      throw new ServiceException("Error while generating a new signing key", e);
    }
  }

  public String issueToken(String userId) {
    SigningKey key = signingKeyService.findSingleWithNamedQuery(SigningKey.FIND_ACTIVE, new HashMap<>());

    if(key == null) {
      return null;
    }
    
    JWSSigner signer = new RSASSASigner(key.getPrivateKey());
    JWTClaimsSet claimsSet = new JWTClaimsSet.Builder().issuer("budget-server")
                                                       .expirationTime(new Date(new Date().getTime() + 60 * 1000))
                                                       .build();

    SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.RS256), claimsSet);

    // Compute the RSA signature
    try {
      signedJWT.sign(signer);
    } catch (JOSEException e) {
      log.warn("Error signing JWT", e);
      return null;
    }

    return signedJWT.serialize();
  }

}
