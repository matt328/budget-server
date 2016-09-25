package org.matt.budget.models.converters;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.persistence.AttributeConverter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PublicKeyConverter implements AttributeConverter<PublicKey, String> {

  @Override
  public String convertToDatabaseColumn(PublicKey attribute) {
    return new String(Base64.getEncoder().encode(attribute.getEncoded()));
  }

  @Override
  public PublicKey convertToEntityAttribute(String dbData) {
    byte[] bytes = Base64.getDecoder().decode(dbData);
    try {
      KeyFactory kf = KeyFactory.getInstance("RSA");
      return kf.generatePublic(new X509EncodedKeySpec(bytes));
    } catch (Exception e) {
      log.warn("Exception loading Public Key", e);
      return null;
    }
  }

}
