package org.matt.budget.rest.endpoints;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.PathParam;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.matt.budget.models.Workspace;
import org.matt.budget.rest.aop.Timed;
import org.matt.budget.rest.common.AccountResource;
import org.matt.budget.rest.common.WorkspaceResource;
import org.matt.budget.service.WorkspaceService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestScoped
public class WorkspaceEndpoint implements WorkspaceResource {

  @Context
  UriInfo uriInfo;

  @Context
  ResourceContext resourceContext;

  @Inject
  WorkspaceService workspaceService;

  @Override
  @Timed
  public AccountResource listAccounts(@PathParam("workspaceId") Integer workspaceId) {
    return resourceContext.getResource(AccountEndpoint.class);
  }

  @Override
  public Response create(Workspace entity) {
    Workspace created = workspaceService.insert(entity);

    return Response.created(getSelfLink(created.getId()).getUri())
                   .entity(created)
                   .links(new Link[] { getSelfLink(created.getId()), getAccountsLink(created.getId()) })
                   .build();
  }

  @Override
  @Timed
  public Response deleteById(@PathParam("workspaceId") Integer workspaceId) {
    return null;
  }

  @Override
  @Timed
  public Response findById(@PathParam("workspaceId") Integer workspaceId, @Context Request request, @Context HttpHeaders headers) {
    Workspace entity;

    log.debug("finding by id: {}", workspaceId);
    entity = workspaceService.findById(workspaceId);
    if (entity == null) {
      log.debug("Caught ObjectNotFoundException");
      return Response.status(Status.NOT_FOUND).build();
    }

    CacheControl cc = new CacheControl();
    cc.setMaxAge(100);

    EntityTag tag = new EntityTag(Integer.toString(entity.hashCode()));

    Response.ResponseBuilder builder = request.evaluatePreconditions(tag);

    if (builder != null) {
      return builder.cacheControl(cc)
                    .build();
    }
    builder = Response.ok(entity)
                      .cacheControl(cc)
                      .links(new Link[] { getSelfLink(entity.getId()), getAccountsLink(entity.getId()) })
                      .tag(tag);

    return builder.build();

  }

  @Override
  @Timed
  public Response list() {
    List<Workspace> workspaces = workspaceService.list();
    return Response.ok(workspaces).build();
  }

  @Override
  @Timed
  public Response update(@PathParam("workspaceId") Integer workspaceId, Workspace entity) {
    if (entity == null) {
      return Response.status(Status.BAD_REQUEST).build();
    }
    if (!workspaceId.equals(entity.getId())) {
      return Response.status(Status.CONFLICT)
                     .entity(entity)
                     .build();
    }

    workspaceService.update(entity);
    return Response.noContent().build();
  }

  private Link getSelfLink(Integer workspaceId) {
    return Link.fromUri(uriInfo.getBaseUriBuilder()
                               .path(WorkspaceResource.class)
                               .path(WorkspaceResource.class, "findById")
                               .build(Integer.toString(workspaceId)))
               .rel("self")
               .build();
  }

  private Link getAccountsLink(Integer workspaceId) {
    return Link.fromUri(uriInfo.getBaseUriBuilder()
                               .path(WorkspaceResource.class)
                               .path(WorkspaceResource.class, "listAccounts")
                               .build(Integer.toString(workspaceId)))
               .rel("accounts")
               .build(Integer.toString(workspaceId));

  }
}
