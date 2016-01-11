package org.matt.budget.persistence.cdi;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.enterprise.context.Dependent;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionPoint;

import org.matt.budget.models.BaseEntity;
import org.matt.budget.persistence.BaseRepository;

public class RepositoryProducer implements Serializable {

	private static final long serialVersionUID = 1L;

	@Produces
	@Dependent
	@Repository
	public <ID, T extends BaseEntity<ID>> BaseRepository<T, ID> produce(InjectionPoint ip, BeanManager bm) {
		if (ip.getAnnotated().isAnnotationPresent(Repository.class)) {

			@SuppressWarnings("unchecked")
			BaseRepository<T, ID> repository = (BaseRepository<T, ID>) this.getBeanByName("baseDao", bm);

			ParameterizedType type = (ParameterizedType) ip.getType();
			Type[] typeArgs = type.getActualTypeArguments();

			@SuppressWarnings("unchecked")
			Class<T> entityClass = (Class<T>) typeArgs[0];

			repository.setEntityClass(entityClass);
			return repository;
		}
		throw new IllegalArgumentException("Annotation @Dao is required when injecting BaseDao");
	}

	public Object getBeanByName(String name, BeanManager bm) {

		Bean<?> bean = bm.getBeans(name).iterator().next();

		CreationalContext<?> ctx = bm.createCreationalContext(bean);

		Object o = bm.getReference(bean, bean.getBeanClass(), ctx);

		return o;
	}

}
