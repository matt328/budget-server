package org.matt.budget.models;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import lombok.Data;

@Data
@MappedSuperclass
public abstract class BaseEntity<ID> {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private ID id;

}
