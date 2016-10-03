package org.matt.budget.rest.endpoints;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.OptimisticLockException;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.matt.budget.models.Account;
import org.matt.budget.rest.aop.Timed;
import org.matt.budget.rest.common.AccountResource;
import org.matt.budget.rest.common.WorkspaceResource;
import org.matt.budget.service.AccountService;
import org.matt.budget.service.WorkspaceService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestScoped
public class AccountEndpoint implements AccountResource {

  @Context
  UriInfo uriInfo;

  @Context
  Request request;

  @Context
  HttpHeaders headers;

  @PathParam("workspaceId")
  private Integer workspaceId;

  @Inject
  AccountService accountService;

  @Inject
  WorkspaceService workspaceService;

  @Override
  @Timed
  public Response create(Account entity) {
    Account created = workspaceService.addAccount(entity, workspaceId);

    return Response.created(getSelfLink(created.getId()).getUri())
                   .entity(entity)
                   .header("ETag", entity.hashCode())
                   .links(new Link[] { getSelfLink(created.getId()) })
                   .build();
  }

  @Override
  public Response deleteById(Integer id) {
    return null;
  }

  @Override
  @Timed
  public Response findById(@PathParam("id") Integer id) {
    Account account;
    try {
      account = accountService.findById(id);
    } catch (Throwable ex) {
      return Response.status(Status.NOT_FOUND).build();
    }

    CacheControl cc = new CacheControl();
    cc.setMaxAge(100);

    return Response.ok(account)
                   .cacheControl(cc)
                   .links(new Link[] { getSelfLink(account.getId()) })
                   .build();
  }

  @Override
  @Timed
  public Response list() {
    log.debug("listing all accounts for Workspace {}", workspaceId);
    List<Account> accounts = accountService.list();
    return Response.ok(accounts, MediaType.APPLICATION_JSON)
                   .build();
  }

  @Override
  @Timed
  public Response update(Integer id, Account entity) {
    if (entity == null) {
      return Response.status(Status.BAD_REQUEST)
                     .build();
    }
    if (!id.equals(entity.getId())) {
      return Response.status(Status.CONFLICT)
                     .entity(entity)
                     .build();
    }
    try {
      accountService.update(entity);
    } catch (OptimisticLockException e) {
      return Response.status(Status.CONFLICT)
                     .entity(e.getEntity())
                     .build();
    }
    return Response.noContent()
                   .build();
  }

  public Integer getWorkspaceId() {
    return workspaceId;
  }

  public void setWorkspaceId(Integer workspaceId) {
    this.workspaceId = workspaceId;
  }

  private Link getSelfLink(Integer accountId) {
    return Link.fromUri(uriInfo.getBaseUriBuilder()
                               .path(WorkspaceResource.class)
                               .path(WorkspaceResource.class, "listAccounts")
                               .path(AccountResource.class, "findById")
                               .build(Integer.toString(workspaceId), Integer.toString(accountId)))
               .rel("self")
               .build();
  }

}
