package org.matt.budget.rest.util;

import static com.jayway.restassured.RestAssured.given;

import java.net.URL;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;
import javax.ws.rs.core.Response.Status;

import org.hamcrest.Matchers;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.CleanupUsingScript;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.matt.budget.rest.common.Credentials;

import com.jayway.restassured.http.ContentType;

@RunWith(Arquillian.class)
@UsingDataSet("users.yml")
@CleanupUsingScript("cleanup.sql")
public class AuthenticationEndpointIT extends BaseRestIT {

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
  public void shouldFailAuthenticationWhenUserNotFound() {
    Credentials credentials = new Credentials("matt2", "megan");
    given().contentType(ContentType.JSON)
           .body(credentials)
           .expect()
           .statusCode(Status.UNAUTHORIZED.getStatusCode())
           .when()
           .post(basePath + "api/authz");
  }

  @Test
  public void shouldFailAuthenticationWhenPasswordIncorrect() {
    Credentials credentials = new Credentials("matt", "megan2");
    given().contentType(ContentType.JSON)
           .body(credentials)
           .expect()
           .statusCode(Status.UNAUTHORIZED.getStatusCode())
           .when()
           .post(basePath + "api/authz");
  }

  @Test
  public void shouldAuthenticate() {
    Credentials credentials = new Credentials("matt", "megan");
    given().contentType(ContentType.JSON)
           .body(credentials)
           .expect()
           .statusCode(Status.OK.getStatusCode())
           .contentType(ContentType.JSON)
           .body("token", Matchers.any(String.class))
           .body("expire-date", Matchers.any(String.class))
           .when()
           .post(basePath + "api/authz");
  }
}
