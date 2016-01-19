package org.matt.budget.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.matt.budget.models.enums.AccountType;
import org.matt.budget.persistence.BaseEntity;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
	@GeneratedValue(strategy = GenerationType.AUTO)
	@XmlElement
	private Integer id;

	@Column(name = "name")
	@XmlElement
	private String name;

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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "workspace_id", nullable = false)
	private Workspace workspace;

	@XmlTransient
	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "account")
	private List<Transaction> transactions;
}
