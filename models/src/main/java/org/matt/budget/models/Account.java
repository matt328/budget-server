package org.matt.budget.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.matt.budget.models.enums.AccountType;

import lombok.Data;

@Data
@Entity
@Table(name = "account")
public class Account {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "name")
	private String name;

	@Column(name = "note")
	private String note;

	@Column(name = "reconcileBalance")
	private BigDecimal reconcileBalance;

	@Column(name = "reconcileDate")
	private LocalDateTime reconcileDate;

	@Column(name = "accountType")
	private AccountType accountType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "workspace_id", nullable = false)
	private Workspace workspace;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "account")
	private List<Transaction> transactions;
}
