package org.matt.budget.rest.util;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

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
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.matt.budget.models.Workspace;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Header;
import com.jayway.restassured.response.Response;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(Arquillian.class)
public class RestIT {

	protected static final String WEB_INF = "src/main/webapp/WEB-INF";

	protected static final String SELF_LINK = "<http://0.0.0.0:8080/test/api/workspaces/1>; rel=\"self\"";
	protected static final String ACCOUNTS_LINK = "<http://0.0.0.0:8080/test/api/workspaces/1/accounts>; rel=\"accounts\"";

	@Deployment
	public static Archive<?> createDeployment() {
		MavenResolverSystem resolver = Maven.resolver();
		WebArchive war = ShrinkWrap	.create(WebArchive.class, "test.war")
																.addPackage(Workspace.class.getPackage())
																.addPackages(true, "org.matt.budget")
																.addAsResource("test-persistence.xml", "META-INF/persistence.xml")
																.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
																.addAsLibraries(resolver.loadPomFromFile("pom.xml").resolve("com.jayway.restassured:rest-assured").withTransitivity().asFile())
																.addAsWebInfResource(new File(WEB_INF, "web.xml"), "web.xml")
																.addAsWebInfResource(new File(WEB_INF, "beans.xml"), "beans.xml");
		log.debug(war.toString(true));
		return war;
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
	public void shouldGetById() throws Exception {
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
		MatcherAssert.assertThat(linkValues, Matchers.contains(SELF_LINK, ACCOUNTS_LINK));
	}

	@Test
	public void shouldCreateWorkspace() {
		Workspace entity = Workspace.builder()
																.name("TestWorkspace")
																.build();
		Response response = given()
																.contentType(ContentType.JSON)
																.body(entity)
																.expect()
																.body("name", Matchers.equalTo("TestWorkspace"))
																.when()
																.post(basePath + "api/workspaces");

		List<String> linkValues = getLinkValues(response);

		MatcherAssert.assertThat(linkValues, Matchers.contains(SELF_LINK, ACCOUNTS_LINK));
	}

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

	private List<String> getLinkValues(Response r) {
		List<Header> links = r.headers().getList("Link");
		List<String> linkValues = links	.stream()
																		.map(l -> l.getValue())
																		.collect(Collectors.toList());
		return linkValues;
	}

}
