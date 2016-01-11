package org.matt.budget.models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "workspace")
@XmlRootElement
public class Workspace extends BaseEntity<Long> {

	@Column(name = "name")
	@XmlElement
	private String name;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "workspace")
	@XmlElement
	private List<Account> accounts;
}
