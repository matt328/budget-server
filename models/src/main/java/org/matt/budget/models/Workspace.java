package org.matt.budget.models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.matt.budget.persistence.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(exclude = { "accounts" })
@Builder
@AllArgsConstructor
@Entity
@Cacheable
@Table(name = "workspace")
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class Workspace implements BaseEntity {

  public Workspace() {
    accounts = new ArrayList<>();
  }

  @Id
  @GeneratedValue(generator = "workspace_seq")
  @SequenceGenerator(name = "workspace_seq", sequenceName = "WORKSPACE_SEQ", allocationSize = 1, initialValue = 100)
  @XmlElement
  private Integer id;

  @Column(name = "name")
  @XmlElement
  private String name;

  @XmlTransient
  @OneToMany(fetch = FetchType.LAZY, mappedBy = "workspace")
  private List<Account> accounts;

}
