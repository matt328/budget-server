package org.matt.budget.persistence;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.matt.budget.models.BaseEntity;

public class Repository<T extends BaseEntity> implements Serializable {

	private static final long serialVersionUID = 1L;

	@PersistenceContext
	protected EntityManager entityManager;

	protected Class<T> entityClass;

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

	public void insert(T t) {
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
	public void Repository(InjectionPoint ip) {
		if (ip != null && ip.getType() != null) {
			try {
				entityClass = resolveEntity(ip);
			} catch (Exception e) {
				throw new IllegalArgumentException("provide entity class at injection point eg: @Inject Crud<Entity> crud");
			}

		} else {
			throw new IllegalArgumentException(
					"Provide entity at injection point ex: @Inject Crud<Entity> crud");
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
