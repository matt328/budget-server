package org.matt.budget.rest;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.OptimisticLockException;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;

import org.matt.budget.models.Account;
import org.matt.budget.rest.common.AccountResource;
import org.matt.budget.service.AccountService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestScoped
public class AccountEndpoint implements AccountResource {

	@PathParam("workspaceId")
	private Integer workspaceId;

	@Inject
	AccountService accountService;

	@Override
	public Response create(Account entity) {
		accountService.insert(entity);
		return Response	.created(UriBuilder
																			.fromResource(AccountEndpoint.class)
																			.path(String.valueOf(entity.getId()))
																			.build())
										.build();
	}

	@Override
	public Response deleteById(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Response findById(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Response list() {
		log.debug("listing all accounts for Workspace {}", workspaceId);
		List<Account> accounts = accountService.list();
		return Response	.ok(accounts, MediaType.APPLICATION_JSON)
										.build();
	}

	@Override
	public Response update(Integer id, Account entity) {
		if (entity == null) {
			return Response	.status(Status.BAD_REQUEST)
											.build();
		}
		if (!id.equals(entity.getId())) {
			return Response	.status(Status.CONFLICT)
											.entity(entity)
											.build();
		}
		try {
			accountService.update(entity);
		} catch (OptimisticLockException e) {
			return Response	.status(Status.CONFLICT)
											.entity(e.getEntity())
											.build();
		}
		return Response	.noContent()
										.build();
	}

	public Integer getWorkspaceId() {
		return workspaceId;
	}

	public void setWorkspaceId(Integer workspaceId) {
		this.workspaceId = workspaceId;
	}

}
