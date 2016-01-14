package org.matt.budget.rest;

import java.util.List;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.persistence.OptimisticLockException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.Response.Status;

import org.matt.budget.models.Account;
import org.matt.budget.rest.common.AccountResource;
import org.matt.budget.service.AccountService;

@Singleton
@ConcurrencyManagement
@Lock(LockType.READ)
public class AccountEndpoint implements AccountResource {

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
		List<Account> accounts = accountService.list();
		return Response	.ok(accounts)
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

}
