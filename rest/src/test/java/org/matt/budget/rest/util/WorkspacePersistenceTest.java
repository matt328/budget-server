package org.matt.budget.rest.util;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;

import java.io.File;
import java.net.URL;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.UserTransaction;
import javax.ws.rs.core.Response.Status;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
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

@RunWith(Arquillian.class)
public class WorkspacePersistenceTest {

	protected static final String WEB_INF = "src/main/webapp/WEB-INF";

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
		System.out.println(war.toString(true));
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
	public void shouldListWorkspaces() throws Exception {
		given()
						.when()
						.get(basePath + "api/workspaces")
						.then().statusCode(Status.OK.getStatusCode())
						.body("", hasSize(4))
						.body("name", hasItem("ferrari spider"));
	}

	//@Test
	@UsingDataSet("workspaces.yml")
	public void shouldFindAllWorkspacessUsingJpqlQuery() throws Exception {
		// given
		String fetchingAllWorkspacesInJpql = "select w from Workspace w order by w.id";

		// when
		System.out.println("Selecting (using JPQL)...");
		List<Workspace> workspaces = em.createQuery(fetchingAllWorkspacesInJpql, Workspace.class).getResultList();

		// then
		System.out.println("Found " + workspaces.size() + " workspaces (using JPQL):");
	}

	//@Test
	@UsingDataSet("workspaces.yml")
	public void shouldFindAllWorkspacesUsingCriteriaApi() throws Exception {
		// given
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Workspace> criteria = builder.createQuery(Workspace.class);

		Root<Workspace> game = criteria.from(Workspace.class);
		criteria.select(game);
		criteria.orderBy(builder.asc(game.get("id")));

		// when
		System.out.println("Selecting (using Criteria)...");
		List<Workspace> workspaces = em.createQuery(criteria).getResultList();

		// then
		System.out.println("Found " + workspaces.size() + " workspaces (using Criteria):");
	}

}
