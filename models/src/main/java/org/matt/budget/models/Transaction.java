package org.matt.budget.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "transaction")
@XmlRootElement
public class Transaction extends BaseEntity<Long> {

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
