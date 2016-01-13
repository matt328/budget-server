package org.matt.budget.persistence;

import javax.ejb.Stateless;

import org.matt.budget.models.Account;

@Stateless
public class AccountService extends RepositoryService<Account> {

	private static final long serialVersionUID = 1L;

}
