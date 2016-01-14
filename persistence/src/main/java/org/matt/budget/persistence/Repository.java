package org.matt.budget.persistence;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.matt.budget.models.BaseEntity;

public class Repository<T extends BaseEntity> implements Serializable {

	private static final long serialVersionUID = 1L;

	@PersistenceContext
	protected EntityManager entityManager;

	protected Class<T> entityClass;

	public List<T> findAll() {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(entityClass);
		
		Root<T> root = cq.from(entityClass);
		cq.select(root);
		
		TypedQuery<T> q = entityManager.createQuery(cq);
		List<T> allT = q.getResultList();
		return allT;
	}

	public T find(Serializable id, Class<T> entityClass) {
		return (T) entityManager.find(entityClass, id);
	}

	public void delete(Serializable id, Class<T> entityClass) {
		Object ref = entityManager.getReference(entityClass, id);
		entityManager.remove(ref);
	}

	public T update(T t) {
		return entityManager.merge(t);
	}

	public void save(T t) {
		entityManager.persist(t);
	}

	@SuppressWarnings("unchecked")
	public Class<T> getEntityClass() {
		if (entityClass == null) {
			entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		}
		return entityClass;
	}

	@Inject
	public void Repository(final InjectionPoint ip) {
		if (ip != null && ip.getType() != null) {
			try {
				entityClass = resolveEntity(ip);
			} catch (Exception e) {
				throw new IllegalArgumentException("provide entity class at injection point eg: @Inject Repository<Entity> repo");
			}

		} else {
			throw new IllegalArgumentException(
					"Provide entity at injection point ex: @Inject Repository<Entity> repo");
		}
	}

	private Class<T> resolveEntity(InjectionPoint ip) {
		ParameterizedType type = (ParameterizedType) ip.getType();
		Type[] typeArgs = type.getActualTypeArguments();
		@SuppressWarnings("unchecked")
		Class<T> entityClass = (Class<T>) typeArgs[0];
		return entityClass;
	}

}
