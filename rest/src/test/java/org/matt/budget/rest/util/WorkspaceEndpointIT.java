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
import org.jboss.arquillian.persistence.CleanupUsingScript;
import org.jboss.arquillian.persistence.PersistenceTest;
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
@CleanupUsingScript("cleanup.sql")
@PersistenceTest
@Cleanup(phase = TestExecutionPhase.AFTER, strategy = CleanupStrategy.USED_ROWS_ONLY)
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
  public void shouldNotGetWorkspace() throws Exception {
    given()
           .expect()
           .statusCode(Status.NOT_FOUND.getStatusCode())
           .when()
           .get(basePath + "api/workspaces/1");
  }

  @Test
  @UsingDataSet("workspaces.yml")
  public void shouldGetWorkspaceById() throws Exception {
    Response response = given()
                               .expect()
                               .statusCode(Status.OK.getStatusCode())
                               .header("Cache-Control", Matchers.is("no-transform, max-age=100"))
                               .body("id", Matchers.is(1))
                               .body("name", Matchers.is("ferrari spider"))
                               .when()
                               .get(basePath + "api/workspaces/1");

    List<String> linkValues = getLinkValues(response);

    Workspace created = response.getBody().as(Workspace.class);

    MatcherAssert.assertThat(linkValues, Matchers.contains(WORKSPACE_SELF_LINK.replace("${workspaceId}", created.getId().toString()), ACCOUNTS_LINK.replace("${workspaceId}", created.getId().toString())));

    String expectedEtag = "\"" + Integer.toString(created.hashCode()) + "\"";
    String actualEtag = getHeaderValue(response, "ETag");
    MatcherAssert.assertThat(actualEtag, Matchers.equalTo(expectedEtag));
  }

  @Test
  @UsingDataSet("workspaces.yml")
  public void shouldFailUpdateWithNullEntity() throws Exception {
    given()
           .contentType(ContentType.JSON)
           .expect()
           .body(Matchers.isEmptyOrNullString())
           .when()
           .put(basePath + "api/workspaces/102");
  }

  @Test
  @UsingDataSet({ "workspaces.yml", "users.yml" })
  public void shouldFailUpdateWithWrongId() throws Exception {

    Workspace entity = Workspace.builder()
                                .name("TestWorkspace")
                                .build();

    Response response = given()
                               .header(getAuthHeader("matt", "megan"))
                               .contentType(ContentType.JSON)
                               .body(entity)
                               .expect()
                               .body("name", Matchers.equalTo("TestWorkspace"))
                               .when()
                               .post(basePath + "api/workspaces");
    Workspace created = response.getBody().as(Workspace.class);
    created.setName("Updated Name");

    given()
           .contentType(ContentType.JSON)
           .body(created)
           .expect()
           .statusCode(Status.CONFLICT.getStatusCode())
           .when()
           .put(basePath + "api/workspaces/102");
  }

  @Test
  @UsingDataSet("workspaces.yml")
  public void shouldCacheWithEtag() throws Exception {
    Response response = given()
                               .expect()
                               .statusCode(Status.OK.getStatusCode())
                               .header("Cache-Control", Matchers.is("no-transform, max-age=100"))
                               .body("id", Matchers.is(1))
                               .body("name", Matchers.is("ferrari spider"))
                               .when()
                               .get(basePath + "api/workspaces/1");

    String etag = getHeaderValue(response, "ETag");

    given()
           .header(Headers.IF_NONE_MATCH_STRING, etag)
           .expect()
           .statusCode(Status.NOT_MODIFIED.getStatusCode())
           .header("Cache-Control", Matchers.is("no-transform, max-age=100"))
           .when()
           .get(basePath + "api/workspaces/1");
  }

  @Test
  @UsingDataSet("users.yml")
  public void shouldCreateWorkspace() {
    Workspace entity = Workspace.builder()
                                .name("TestWorkspaceCreated")
                                .build();

    Response response = given()
                               .contentType(ContentType.JSON)
                               .header(getAuthHeader("matt", "megan"))
                               .body(entity)
                               .expect()
                               .body("name", Matchers.equalTo("TestWorkspaceCreated"))
                               .when()
                               .post(basePath + "api/workspaces");

    List<String> linkValues = getLinkValues(response);

    Workspace created = response.getBody().as(Workspace.class);

    String expectedWorkspaceLocation = WORKSPACE_URI.replace("${workspaceId}", created.getId().toString());

    MatcherAssert.assertThat(linkValues, Matchers.contains(WORKSPACE_SELF_LINK.replace("${workspaceId}", created.getId().toString()), ACCOUNTS_LINK.replace("${workspaceId}", created.getId().toString())));

    String location = getHeaderValue(response, Headers.LOCATION_STRING);

    MatcherAssert.assertThat(location, Matchers.equalTo(expectedWorkspaceLocation));

  }

  @Test
  @UsingDataSet("workspaces.yml")
  public void shouldUpdateWorkspace() {
    Workspace entity = Workspace.builder()
                                .id(1)
                                .name("TestWorkspaceUpdated")
                                .build();

    given()
           .contentType(ContentType.JSON)
           .body(entity)
           .expect()
           .statusCode(Status.NO_CONTENT.getStatusCode())
           .when()
           .put(basePath + "api/workspaces/1");

    given()
           .expect()
           .statusCode(Status.OK.getStatusCode())
           .header("Cache-Control", Matchers.is("no-transform, max-age=100"))
           .body("id", Matchers.is(1))
           .body("name", Matchers.is("TestWorkspaceUpdated"))
           .when()
           .get(basePath + "api/workspaces/1");
  }

}
