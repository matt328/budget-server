package org.matt.budget.rest.common;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

import org.matt.budget.models.Workspace;

@Path("/workspaces")
@Produces(MediaType.APPLICATION_JSON)
public interface WorkspaceResource {

	@Path("/{workspaceId:[0-9][0-9]*}/accounts")
	public AccountResource listAccounts(@PathParam("workspaceId") Integer workspaceId);

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response create(Workspace entity);

	@DELETE
	@Path("/{workspaceId:[0-9][0-9]*}")
	public Response deleteById(@PathParam("workspaceId") Integer workspaceId);

	@GET
	@Path("/{workspaceId:[0-9][0-9]*}")
	public Response findById(@PathParam("workspaceId") Integer workspaceId, @Context Request request,@Context HttpHeaders headers);

	@GET
	public Response list();

	@PUT
	@Path("/{workspaceId:[0-9][0-9]*}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response update(@PathParam("workspaceId") Integer workspaceId, Workspace entity);

}
