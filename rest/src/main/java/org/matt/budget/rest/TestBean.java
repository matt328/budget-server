package org.matt.budget.rest;

import javax.inject.Inject;
import javax.inject.Named;

import org.matt.budget.models.Account;
import org.matt.budget.persistence.BaseRepository;
import org.matt.budget.persistence.cdi.Repository;

@Named
public class TestBean {

	@Inject
	@Repository
	private BaseRepository<Account, Long> accountRepository;
	
}
