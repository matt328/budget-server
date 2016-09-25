package org.matt.budget.models;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.matt.budget.persistence.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Cacheable
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@Table(name = "roles", indexes = { @Index(name = "idx_name", columnList = "name", unique = true) })
@NamedQuery(name = Role.FIND_BY_NAME, query = Role.FIND_BY_NAME_QUERY)
public class Role implements BaseEntity {

  public static final String COL_ID = "id";
  public static final String COL_NAME = "name";

  public static final String FIND_BY_NAME = "findByName";
  public static final String FIND_BY_NAME_QUERY = "SELECT r FROM Role r WHERE r.name = :" + COL_NAME;

  @Id
  @Column(name = COL_ID)
  @GeneratedValue(generator = "role_seq")
  @SequenceGenerator(name = "role_seq", sequenceName = "ROLE_SEQ", allocationSize = 1, initialValue = 100)
  private Integer id;

  @Column(name = COL_NAME)
  @XmlElement
  private String name;

}
