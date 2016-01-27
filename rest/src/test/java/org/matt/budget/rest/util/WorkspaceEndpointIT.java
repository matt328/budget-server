package org.matt.budget.rest.util;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;

import java.net.URL;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;
import javax.ws.rs.core.Response.Status;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.Cleanup;
import org.jboss.arquillian.persistence.CleanupStrategy;
import org.jboss.arquillian.persistence.TestExecutionPhase;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.matt.budget.models.Workspace;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;

import io.undertow.util.Headers;

@RunWith(Arquillian.class)
public class WorkspaceEndpointIT extends BaseRestIT {

  @Deployment
  public static Archive<?> createDeployment() {
    return buildWar();
  }

  @ArquillianResource
  URL basePath;

  @PersistenceContext
  EntityManager em;

  @Inject
  UserTransaction utx;

  @Test
  @UsingDataSet("workspaces.yml")
  @Cleanup(phase = TestExecutionPhase.AFTER, strategy = CleanupStrategy.USED_ROWS_ONLY)
  public void shouldListWorkspaces() throws Exception {
    given()
           .when()
           .get(basePath + "api/workspaces")
           .then().statusCode(Status.OK.getStatusCode())
           .body("", hasSize(4))
           .body("name", hasItem("ferrari spider"))
           .body("name", hasItem("mustang spider"))
           .body("name", hasItem("porche avenger"))
           .body("name", hasItem("porche rally"));
  }

  @Test
  @UsingDataSet("workspaces.yml")
  @Cleanup(phase = TestExecutionPhase.AFTER, strategy = CleanupStrategy.USED_ROWS_ONLY)
  public void shouldGetWorkspaceById() throws Exception {
    Response response = given()
                               .expect()
                               .statusCode(Status.OK.getStatusCode())
                               .header("ETag", Matchers.anything())
                               .header("Cache-Control", Matchers.is("no-transform, max-age=100"))
                               .body("id", Matchers.is(1))
                               .body("name", Matchers.is("ferrari spider"))
                               .when()
                               .get(basePath + "api/workspaces/1");
    List<String> linkValues = getLinkValues(response);
    Workspace created = response.getBody().as(Workspace.class);
    MatcherAssert.assertThat(linkValues, Matchers.contains(WORKSPACE_SELF_LINK.replace("${workspaceId}", created.getId().toString()), ACCOUNTS_LINK.replace("${workspaceId}", created.getId().toString())));
  }

  @Test
  public void shouldCreateWorkspace() {
    Workspace entity = Workspace.builder()
                                .name("TestWorkspace")
                                .build();

    String expectedWorkspaceLocation = WORKSPACE_URI.replace("${workspaceId}", "100");

    Response response = given()
                               .contentType(ContentType.JSON)
                               .body(entity)
                               .expect()
                               .body("name", Matchers.equalTo("TestWorkspace"))
                               .header(Headers.LOCATION_STRING, Matchers.equalTo(expectedWorkspaceLocation))
                               .when()
                               .post(basePath + "api/workspaces");

    List<String> linkValues = getLinkValues(response);
    Workspace created = response.getBody().as(Workspace.class);
    MatcherAssert.assertThat(linkValues, Matchers.contains(WORKSPACE_SELF_LINK.replace("${workspaceId}", created.getId().toString()), ACCOUNTS_LINK.replace("${workspaceId}", created.getId().toString())));
  }

}
