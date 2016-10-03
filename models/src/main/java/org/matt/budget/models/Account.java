package org.matt.budget.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.matt.budget.models.enums.AccountType;
import org.matt.budget.persistence.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
@Entity
@Table(name = "account")
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class Account implements BaseEntity {

  public Account() {
    transactions = new ArrayList<>();
  }

  @Id
  @GeneratedValue(generator = "account_seq")
  @SequenceGenerator(name = "account_seq", sequenceName = "ACCOUNT_SEQ", allocationSize = 1, initialValue = 100)
  @XmlElement
  private Integer id;

  @NotNull
  @Size(min = 1, max = 32)
  @Column(name = "name")
  @XmlElement
  private String name;

  @Size(max = 255)
  @Column(name = "note")
  @XmlElement
  private String note;

  @Column(name = "reconcileBalance")
  @XmlElement
  private BigDecimal reconcileBalance;

  @Column(name = "reconcileDate")
  @XmlElement
  private LocalDateTime reconcileDate;

  @Column(name = "accountType")
  @XmlElement
  private AccountType accountType;

  @XmlTransient
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "workspace_id")
  private Workspace workspace;

  @XmlTransient
  @OneToMany(fetch = FetchType.LAZY, mappedBy = "account")
  private List<Transaction> transactions;
}
