package org.matt.budget.models.converters;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

import javax.persistence.AttributeConverter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PrivateKeyConverter implements AttributeConverter<PrivateKey, String> {

  @Override
  public String convertToDatabaseColumn(PrivateKey attribute) {
    return new String(Base64.getEncoder().encode(attribute.getEncoded()));
  }

  @Override
  public PrivateKey convertToEntityAttribute(String dbData) {
    byte[] bytes = Base64.getDecoder().decode(dbData);
    try {
      KeyFactory kf = KeyFactory.getInstance("RSA");
      return kf.generatePrivate(new PKCS8EncodedKeySpec(bytes));
    } catch (Exception e) {
      log.warn("Exception loading Private Key", e);
      return null;
    }
  }

}
