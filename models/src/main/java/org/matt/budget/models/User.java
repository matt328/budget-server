package org.matt.budget.models;

import java.util.List;

import javax.enterprise.inject.Vetoed;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
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
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Vetoed
@Cacheable
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@Table(name = "users", indexes = { @Index(name = "idx_user_id", columnList = "userId", unique = true) })
@NamedQuery(name = User.FIND_BY_USER_ID, query = User.FIND_BY_USER_ID_QUERY)
public class User implements BaseEntity {

  public static final String COL_ID = "id";
  public static final String COL_NAME = "name";
  public static final String COL_USER_ID = "userId";
  public static final String COL_PASSWORD_HASH = "passwordHash";

  public static final String FIND_BY_USER_ID = "findUserByUserId";
  public static final String FIND_BY_USER_ID_QUERY = "SELECT u FROM User u WHERE u.userId = :" + COL_USER_ID;

  @Id
  @Column(name = COL_ID)
  @GeneratedValue(generator = "user_seq")
  @SequenceGenerator(name = "user_seq", sequenceName = "USER_SEQ", allocationSize = 1, initialValue = 100)
  private Integer id;

  @Column(name = COL_NAME)
  @XmlElement
  private String name;

  @Column(name = COL_USER_ID)
  @XmlElement
  private String userId;

  @Column(name = COL_PASSWORD_HASH)
  @XmlElement
  private String passwordHash;

  @JsonIgnore
  @XmlTransient
  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
  private List<Role> roles;
}
