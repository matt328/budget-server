package org.matt.budget.rest;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.OptimisticLockException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;

import org.matt.budget.models.Workspace;
import org.matt.budget.rest.common.AccountResource;
import org.matt.budget.rest.common.WorkspaceResource;
import org.matt.budget.service.WorkspaceService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestScoped
@Path("/workspaces")
public class WorkspaceEndpoint implements WorkspaceResource {

	@Context
	ResourceContext rc;

	@Inject
	WorkspaceService workspaceService;

	@Override
	public AccountResource listAccounts(@PathParam("workspaceId") Integer workspaceId) {
		return rc.getResource(AccountEndpoint.class);
	}

	@Override
	public Response create(Workspace entity) {
		workspaceService.insert(entity);
		System.out.println("Creating workspace");
		log.debug("Creating Workspace: {}", entity);
		return Response	.created(UriBuilder
																			.fromResource(WorkspaceEndpoint.class)
																			.path(String.valueOf(entity.getId()))
																			.build())
										.build();
	}

	@Override
	public Response deleteById(@PathParam("workspaceId") Integer workspaceId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Response findById(@PathParam("workspaceId") Integer workspaceId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Response list() {
		List<Workspace> workspaces = workspaceService.list();
		return Response	.ok(workspaces)
										.build();
	}

	@Override
	public Response update(@PathParam("workspaceId") Integer workspaceId, Workspace entity) {
		if (entity == null) {
			return Response	.status(Status.BAD_REQUEST)
											.build();
		}
		if (!workspaceId.equals(entity.getId())) {
			return Response	.status(Status.CONFLICT)
											.entity(entity)
											.build();
		}
		try {
			workspaceService.update(entity);
		} catch (OptimisticLockException e) {
			return Response	.status(Status.CONFLICT)
											.entity(e.getEntity())
											.build();
		}
		return Response	.noContent()
										.build();
	}

}
