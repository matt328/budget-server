package org.matt.budget.persistence.cdi;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

@Qualifier
@Retention(RUNTIME)
@Target({ ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD, ElementType.TYPE })
public @interface Repository {

}
