package org.matt.budget.rest.common;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.matt.budget.models.Account;

@Path("/accounts")
@Produces("application/json")
public interface AccountResource {

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response create(Account entity);

	@DELETE
	@Path("/{id:[0-9][0-9]*}")
	public Response deleteById(@PathParam("id") Integer id);

	@GET
	@Path("/{id:[0-9][0-9]*}")
	public Response findById(@PathParam("id") Integer id);

	@GET
	public Response list();

	@PUT
	@Path("/{id:[0-9][0-9]*}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response update(@PathParam("id") Integer id, Account entity);

}
