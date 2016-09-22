package org.matt.budget.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.matt.budget.persistence.BaseEntity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Entity
@Table(name = "transaction")
@XmlRootElement
public class Transaction implements BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  @Column(name = "payee")
  @XmlElement
  private String payee;

  @Column(name = "note")
  @XmlElement
  private String note;

  @Column(name = "amount")
  @XmlElement
  private BigDecimal amount;

  @Column(name = "transactionDate")
  @XmlElement
  private LocalDateTime transactionDate;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "account_id", nullable = false)
  @XmlElement
  private Account account;
}
