package org.matt.budget.persistence;

import java.io.Serializable;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.matt.budget.models.BaseEntity;

@Named
@Stateless
public class Repo<T extends BaseEntity<ID>, ID> implements Serializable {

	private static final long serialVersionUID = 1L;

	@PersistenceContext
	private EntityManager entityManager;

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public T find(ID id, Class<T> entityClass) {
		return (T) entityManager.find(entityClass, id);
	}

	public void delete(ID id, Class<T> entityClass) {
		Object ref = entityManager.getReference(entityClass, id);
		entityManager.remove(ref);
	}

	public T update(T t) {
		return entityManager.merge(t);
	}

	public void insert(T t) {
		entityManager.persist(t);
	}

	
	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<T> findAll(Class<T> entityClass) {
		return entityManager.createQuery("Select entity FROM " + entityClass.getSimpleName() + " entity").getResultList();
	}
}
