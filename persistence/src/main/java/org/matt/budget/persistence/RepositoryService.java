package org.matt.budget.persistence;

import java.io.Serializable;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.matt.budget.models.BaseEntity;

@Dependent
public abstract class RepositoryService<T extends BaseEntity> implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Repository<T> repo;
	
}
