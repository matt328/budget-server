package org.matt.budget.models;

import java.security.PrivateKey;
import java.security.PublicKey;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.matt.budget.models.converters.PrivateKeyConverter;
import org.matt.budget.models.converters.PublicKeyConverter;
import org.matt.budget.persistence.BaseEntity;

import lombok.Data;

@Data
@Entity
@Cacheable
@Table(name="signingkey")
@NamedQuery(name = SigningKey.FIND_ACTIVE, query = SigningKey.FIND_ACTIVE_QUERY)
public class SigningKey implements BaseEntity {

  public static final String COL_ID = "id";
  public static final String COL_PRIVATE = "private";
  public static final String COL_PUBLIC = "public";
  public static final String COL_ACTIVE = "active";
  
  public static final String FIND_ACTIVE = "findActive";
  public static final String FIND_ACTIVE_QUERY = "SELECT k FROM SigningKey k WHERE k.active = true";
  
  @Id
  @GeneratedValue(generator = "signer_seq")
  @SequenceGenerator(name = "signer_seq", sequenceName = "SIGNER_SEQ", allocationSize = 1, initialValue = 100)
  private Integer id;
  
  @Column(name=COL_PRIVATE, columnDefinition="TEXT")
  @Convert(converter = PrivateKeyConverter.class)
  private PrivateKey privateKey;
  
  @Column(name=COL_PUBLIC, columnDefinition="TEXT")
  @Convert(converter = PublicKeyConverter.class)
  private PublicKey publicKey;
  
  @Column(name=COL_ACTIVE)
  private boolean active;
}
