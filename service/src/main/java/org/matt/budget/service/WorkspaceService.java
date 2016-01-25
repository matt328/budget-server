package org.matt.budget.service;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.matt.budget.models.Account;
import org.matt.budget.models.Workspace;

@Stateless
public class WorkspaceService extends RepositoryService<Workspace> {

	private static final long serialVersionUID = 1L;

	@Inject
	AccountService accountService;

	public Account addAccount(Account account, Integer workspaceId) {

		Workspace workspace = findById(workspaceId);

		account.setWorkspace(workspace);

		accountService.insert(account);

		workspace.getAccounts().add(account);

		repo().update(workspace);

		return account;
	}

}
