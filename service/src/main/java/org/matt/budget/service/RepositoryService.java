package org.matt.budget.service;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import org.matt.budget.persistence.BaseEntity;
import org.matt.budget.persistence.Repository;
import org.matt.budget.service.exception.InvalidDataException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Dependent
public abstract class RepositoryService<T extends BaseEntity> implements Serializable {

  private static final long serialVersionUID = 1L;

  @Inject
  private Repository<T> repo;

  @TransactionAttribute(TransactionAttributeType.SUPPORTS)
  public Repository<T> repo() {
    return repo;
  }

  public T findSingleWithNamedQuery(String queryName, Map<String, Object> queryParameters) {
    try {
      return repo.findSingleWithNamedQuery(queryName, queryParameters);
    } catch (NoResultException e) {
      log.debug("No Results for findSingleWithNamedQuery, queryName: {}, params: {}", queryName, queryParameters);
      return null;
    }
  }

  public T findById(Serializable id) {
    try {
      return repo.find(id);
    } catch (NoResultException e) {
      String message = MessageFormat.format("Entity with id: {0} was not found", id);
      log.warn(message);
      return null;
    }
  }

  public List<T> list() {
    return repo.findAll();
  }

  public T insert(T entity) throws InvalidDataException {
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
