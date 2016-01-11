package org.matt.budget.persistence;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.inject.Named;

import org.matt.budget.models.BaseEntity;

import lombok.Data;

@Data
@Named
@Stateless
public class BaseRepository<T extends BaseEntity<ID>, ID> implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	protected Repo<T, ID> repo;

	private Class<T> entityClass;

	@SuppressWarnings("unchecked")
	public Class<T> getEntityClass() {
		if (entityClass == null) {
			// only works if one extends BaseDao, we will take care of it with CDI
			entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		}
		return entityClass;
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public T find(ID id) {
		return repo.find(id, getEntityClass());
	}

	public void delete(ID id) {
		repo.delete(id, getEntityClass());
	}

	public T update(T t) {
		return repo.update(t);
	}

	public void insert(T t) {
		repo.insert(t);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<T> findAll() {
		return repo.findAll(getEntityClass());
	}
}
