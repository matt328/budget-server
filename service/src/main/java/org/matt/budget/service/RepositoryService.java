package org.matt.budget.service;

import java.io.Serializable;
import java.util.List;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.matt.budget.persistence.BaseEntity;
import org.matt.budget.persistence.Repository;
import org.matt.budget.service.exception.InvalidDataException;

@Dependent
public abstract class RepositoryService<T extends BaseEntity> implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Repository<T> repo;

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Repository<T> repo() {
		return repo;
	}
	
	public T findById(Serializable id) {
		return repo.find(id);
	}
	
	public List<T> list() {
		return repo.findAll();
	}

	public T insert(T entity) {
		if (entity == null) {
			throw new InvalidDataException("Entity cannot be null");
		}

		if (entity.getId() != null) {
			throw new InvalidDataException("Entity must be transient");
		}

		beforeInsert(entity);
		T created = repo().save(entity);
		afterInsert(entity);
		
		return created;
	}

	public void update(T entity) {
		if (entity == null) {
			throw new InvalidDataException("Entity cannot be null");
		}
		if (entity.getId() == null) {
			throw new InvalidDataException("Entity cannot be transient");
		}
		beforeUpdate(entity);
		repo().update(entity);
		afterUpdate(entity);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	private void afterInsert(T entity) {

	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	private void beforeInsert(T entity) {

	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	private void afterUpdate(T entity) {

	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	private void beforeUpdate(T entity) {

	}

}
