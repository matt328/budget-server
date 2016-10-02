package org.matt.budget.models;

import javax.enterprise.inject.Vetoed;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.matt.budget.persistence.BaseEntity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Vetoed
@Cacheable
@XmlRootElement
@EqualsAndHashCode(exclude = { "user" })
@XmlAccessorType(XmlAccessType.NONE)
@Table(name = "tokens", indexes = { @Index(name = "idx_token_id", columnList = "tokenId", unique = true) })
@NamedQuery(name = Token.FIND_BY_TOKEN_ID, query = Token.FIND_BY_TOKEN_ID_QUERY)
public class Token implements BaseEntity {

  public static final String COL_ID = "id";
  public static final String COL_TOKEN_ID = "tokenId";
  public static final String COL_DEVICE = "device";
  public static final String COL_REVOKED = "revoked";
  public static final String COL_USER = "user_id";

  public static final String FIND_BY_TOKEN_ID = "findByTokenId";
  public static final String FIND_BY_TOKEN_ID_QUERY = "SELECT t FROM Token t WHERE t.tokenId = :" + COL_TOKEN_ID;

  @Id
  @Column(name = COL_ID)
  @GeneratedValue(generator = "user_seq")
  @SequenceGenerator(name = "user_seq", sequenceName = "USER_SEQ", allocationSize = 1, initialValue = 100)
  private Integer id;

  @Column(name = COL_TOKEN_ID)
  @XmlElement
  private String tokenId;

  @Column(name = COL_DEVICE)
  @XmlElement
  private String device;

  @Column(name = COL_REVOKED)
  @XmlElement
  private Boolean revoked;
  
  @XmlTransient
  @JsonIgnore
  @ManyToOne
  @JoinColumn(name = COL_USER)
  private User user;
}

/*TODO Future Work:
 * - When a user acquires a new token, store a record of that here, mapped by the jti
 * - Also somehow capture a device string (fingerprintjs2)
 * - when verifying tokens, verify that it exists in this list, has not been revoked, and matches
 * the device they are accessing from.
 */
