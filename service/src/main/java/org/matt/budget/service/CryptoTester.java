package org.matt.budget.service;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.text.ParseException;
import java.util.Base64;
import java.util.Date;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

public class CryptoTester {

  public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException, JOSEException, ParseException {
    KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
    kpg.initialize(2048);
    KeyPair keyPair = kpg.generateKeyPair();

    PublicKey pub = keyPair.getPublic();
    PrivateKey prv = keyPair.getPrivate();

    String publicString = new String(Base64.getEncoder().encode(pub.getEncoded()));
    String privateString = new String(Base64.getEncoder().encode(prv.getEncoded()));

    // now save Strings

    // to recover the key
    byte[] newPrivateBytes = Base64.getDecoder().decode(privateString);
    byte[] newPublicBytes = Base64.getDecoder().decode(publicString);

    KeyFactory kf = KeyFactory.getInstance("RSA");
    PrivateKey privateKey = kf.generatePrivate(new PKCS8EncodedKeySpec(newPrivateBytes));
    PublicKey publicKey = kf.generatePublic(new X509EncodedKeySpec(newPublicBytes));

    System.out.println("-----BEGIN RSA PUBLIC KEY-----");
    System.out.println(publicString);
    System.out.println("-----END RSA PUBLIC KEY-----");

    System.out.println("");

    System.out.println("-----BEGIN RSA PRIVATE KEY-----");
    System.out.println(privateString);
    System.out.println("-----END RSA PRIVATE KEY-----");

    JWSSigner signer = new RSASSASigner(privateKey);

    JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                                                       .subject("matt")
                                                       .issuer("budget-server")
                                                       .expirationTime(new Date(new Date().getTime() + 24 * 60 * 60 * 1000))
                                                       .build();

    SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.RS256), claimsSet);

    // Compute the RSA signature
    signedJWT.sign(signer);

    String s = signedJWT.serialize();

    System.out.println("JWT: " + s);

    // On the consumer side, parse the JWS and verify its RSA signature
    signedJWT = SignedJWT.parse(s);

    JWSVerifier verifier = new RSASSAVerifier((RSAPublicKey) publicKey);
    if (signedJWT.verify(verifier)) {
      System.out.println("JWT Verified");
      System.out.println("Subject: " + signedJWT.getJWTClaimsSet().getSubject());
      System.out.println("Issuer: " + signedJWT.getJWTClaimsSet().getIssuer());
      System.out.println("ExpirationTime: " + signedJWT.getJWTClaimsSet().getExpirationTime().toString());
    }

    RSAKey jwk = new RSAKey.Builder((RSAPublicKey) publicKey).build();

    System.out.println("jwk: " + jwk.toJSONString());
  }

}
