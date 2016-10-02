package org.matt.budget.service;

import java.io.Serializable;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.matt.budget.models.SigningKey;
import org.matt.budget.models.Token;
import org.matt.budget.models.User;
import org.matt.budget.service.encryption.BCryptEncryption;
import org.matt.budget.service.encryption.EncryptionService;
import org.matt.budget.service.exception.ServiceException;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Stateless
public class AuthService implements Serializable {

  private static final long serialVersionUID = 1L;

  private static final Duration JWT_MAX_AGE = Duration.ofDays(14);

  @Inject
  UserService userService;

  @Inject
  SigningKeyService signingKeyService;

  @Inject
  @BCryptEncryption
  EncryptionService encryptionService;

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

  public String issueToken(String userId, String device) {
    SigningKey key = signingKeyService.findSingleWithNamedQuery(SigningKey.FIND_ACTIVE, new HashMap<>());

    if (key == null) {
      return null;
    }

    JWSSigner signer = new RSASSASigner(key.getPrivateKey());

    String jti = UUID.randomUUID().toString().replaceAll("-", "");

    JWTClaimsSet claimsSet = new JWTClaimsSet.Builder().subject(userId)
                                                       .jwtID(jti)
                                                       .issuer("budget-server")
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

    Token token = Token.builder().tokenId(jti)
                       .device(device)
                       .build();

    userService.addTokenToUser(token, userId);

    return signedJWT.serialize();
  }

  public String extractUser(String token) {
    SigningKey key = signingKeyService.findSingleWithNamedQuery(SigningKey.FIND_ACTIVE, new HashMap<>());

    if (key == null) {
      log.debug("Unable to verify token, no signing key could be found");
      return null;
    }

    SignedJWT jwt = null;
    try {
      jwt = SignedJWT.parse(token);
    } catch (ParseException e) {
      log.warn("Failed to parse token", e);
      return null;
    }

    try {
      return jwt.getJWTClaimsSet().getSubject();
    } catch (ParseException e) {
      log.warn("Failed to extract subject from claims");
      return null;
    }

  }

  public boolean isTokenValid(String token) {

    SigningKey key = signingKeyService.findSingleWithNamedQuery(SigningKey.FIND_ACTIVE, new HashMap<>());

    if (key == null) {
      log.debug("Unable to verify token, no signing key could be found");
      return false;
    }

    SignedJWT jwt = null;
    try {
      jwt = SignedJWT.parse(token);
    } catch (ParseException e) {
      log.warn("Failed to parse token", e);
      return false;
    }

    JWSVerifier verifier = new RSASSAVerifier((RSAPublicKey) key.getPublicKey());

    try {
      if (!jwt.verify(verifier)) {
        log.warn("Token Verification Failed, signature verification");
        return false;
      }
    } catch (JOSEException e) {
      log.warn("Exception Verifying Token", e);
      return false;
    }

    JWSHeader jwtHeader = jwt.getHeader();

    // Verify Algorithm
    if (!JWSAlgorithm.RS256.equals(jwtHeader.getAlgorithm())) {
      log.warn("Token Verification Failed, wrong algorithm used");
      return false;
    }

    // Verify Claims
    try {
      if (!verifyJWTClaims(jwt.getJWTClaimsSet())) {
        return false;
      }
    } catch (ParseException e) {
      log.warn("Error reading claims", e);
      return false;
    }

    // Blacklist
    if (isJWTBlacklisted(jwt)) {
      return false;
    }

    return true;
  }

  private boolean isJWTBlacklisted(SignedJWT jwt) {
    return false;
  }

  private boolean verifyJWTClaims(JWTClaimsSet claims) {
    Instant now = Instant.now();
    // Issuer
    if (!"budget-server".equals(claims.getIssuer())) {
      log.warn("Token Verification Failed, invalid issuer");
      return false;
    }

    // Expiration Time
    Instant exipres = claims.getExpirationTime().toInstant();
    if (new Date().toInstant().isAfter(exipres)) {
      log.warn("Token Verification Failed, token is expired");
      return false;
    }

    // Max Age
    Duration expiresIn = Duration.between(now, exipres);
    if (expiresIn.getSeconds() > JWT_MAX_AGE.getSeconds()) {
      log.warn("JWT expiration is too far in the future.  '" + expiresIn + "' is greater than system max: " + JWT_MAX_AGE);
      return false;
    }

    if (claims.getIssueTime() != null) {
      Instant issue = claims.getIssueTime().toInstant();

      Duration issuedAt = Duration.between(issue, now);
      if (issuedAt.getSeconds() > JWT_MAX_AGE.getSeconds()) {
        log.warn("JWT was issued too far in the past.  '" + issuedAt + "' is greater than system max: " + JWT_MAX_AGE);
        return false;
      }
    }

    return true;
  }

}
