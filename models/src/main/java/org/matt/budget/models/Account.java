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
import org.matt.budget.persistence.BaseEntity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Entity
@Table(name = "account")
public class Account implements BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

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
