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
import org.matt.budget.models.Account;
import org.matt.budget.models.enums.AccountType;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;

import io.undertow.util.Headers;

@RunWith(Arquillian.class)
public class AccountEndpointIT extends BaseRestIT {

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
  public void shouldListAccounts() {
    Integer workspaceId = 1;
    given()
           .when()
           .get(basePath + "api/workspaces/" + workspaceId + "/accounts")
           .then().statusCode(Status.OK.getStatusCode())
           .body("", hasSize(3))
           .body("name", hasItem("Test Account 1"))
           .body("name", hasItem("Test Account 2"))
           .body("name", hasItem("Test Account 3"));
  }

  @Test
  @UsingDataSet("workspaces.yml")
  @Cleanup(phase = TestExecutionPhase.AFTER, strategy = CleanupStrategy.USED_TABLES_ONLY)
  public void shouldCreateAccount() {
    Account account = Account.builder()
                             .accountType(AccountType.OFF_BUDGET)
                             .name("Test Account")
                             .note("Test Account Note")
                             .build();
    Integer workspaceId = 1;
    String expectedLocationHeader = ACCOUNTS_URI.replace("${workspaceId}", workspaceId.toString()).replace("${accountId}", "100");

    Response response = given()
                               .contentType(ContentType.JSON)
                               .body(account)
                               .expect()
                               .body("name", Matchers.equalTo("Test Account"))
                               .header(Headers.LOCATION_STRING, Matchers.equalTo(expectedLocationHeader))
                               .when()
                               .post(basePath + "api/workspaces/" + workspaceId.toString() + "/accounts");
    List<String> linkValues = getLinkValues(response);
    Account created = response.getBody().as(Account.class);

    String expectedSelfLink = ACCOUNT_SELF_LINK.replace("${accountId}", created.getId().toString()).replace("${workspaceId}", workspaceId.toString());

    MatcherAssert.assertThat(linkValues, Matchers.contains(expectedSelfLink));
  }

  @Test
  @UsingDataSet("workspaces.yml")
  @Cleanup(phase = TestExecutionPhase.AFTER, strategy = CleanupStrategy.USED_TABLES_ONLY)
  public void shouldGetAccountById() {
    Response response = given()
                               .expect()
                               .statusCode(Status.OK.getStatusCode())
                               .header("Cache-Control", Matchers.is("no-transform, max-age=100"))
                               .body("id", Matchers.is(2))
                               .body("name", Matchers.is("Test Account 1"))
                               .when()
                               .get(basePath + "api/workspaces/1/accounts/2");
    List<String> linkValues = getLinkValues(response);
    String selfLink = ACCOUNT_SELF_LINK.replace("${accountId}", "2").replace("${workspaceId}", "1");
    MatcherAssert.assertThat(linkValues, Matchers.contains(selfLink));
  }

}
