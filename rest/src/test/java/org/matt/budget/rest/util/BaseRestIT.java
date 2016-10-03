package org.matt.budget.rest.util;

import static com.jayway.restassured.RestAssured.given;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.core.Response.Status;

import org.hamcrest.Matchers;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;
import org.matt.budget.models.Workspace;
import org.matt.budget.rest.common.Credentials;

import com.google.common.net.HttpHeaders;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Header;
import com.jayway.restassured.response.Response;

public abstract class BaseRestIT {

  @ArquillianResource
  URL basePath;

  protected static final String WEB_INF = "src/main/webapp/WEB-INF";
  protected static final String HOST = "127.0.0.1";
  protected static final String PORT = "8080";

  protected static final String ACCOUNTS_LINK = "<http://" + HOST + ":" + PORT + "/test/api/workspaces/${workspaceId}/accounts>; rel=\"accounts\"";
  protected static final String ACCOUNT_SELF_LINK = "<http://" + HOST + ":" + PORT + "/test/api/workspaces/${workspaceId}/accounts/${accountId}>; rel=\"self\"";
  protected static final String ACCOUNTS_URI = "http://" + HOST + ":" + PORT + "/test/api/workspaces/${workspaceId}/accounts/${accountId}";

  protected static final String WORKSPACE_SELF_LINK = "<http://" + HOST + ":" + PORT + "/test/api/workspaces/${workspaceId}>; rel=\"self\"";
  protected static final String WORKSPACE_URI = "http://" + HOST + ":" + PORT + "/test/api/workspaces/${workspaceId}";

  protected static Archive<?> buildWar() {
    MavenResolverSystem resolver = Maven.resolver();
    WebArchive war = ShrinkWrap.create(WebArchive.class, "test.war")
                               .addPackage(Workspace.class.getPackage())
                               .addPackages(true, "org.matt.budget")
                               .addAsResource("messages.properties", "messages.properties")
                               .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
                               .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                               .addAsLibraries(resolver.loadPomFromFile("pom.xml").resolve("com.jayway.restassured:rest-assured").withTransitivity().asFile())
                               .addAsLibraries(resolver.loadPomFromFile("pom.xml").resolve("org.mindrot:jbcrypt").withTransitivity().asFile())
                               .addAsLibraries(resolver.loadPomFromFile("pom.xml").resolve("com.nimbusds:nimbus-jose-jwt").withTransitivity().asFile())
                               .addAsWebInfResource(new File(WEB_INF, "web.xml"), "web.xml")
                               .addAsWebInfResource(new File(WEB_INF, "beans.xml"), "beans.xml")
                               .addAsWebInfResource(new File(WEB_INF, "ejb-jar.xml"), "ejb-jar.xml");
    return war;
  }

  protected List<String> getLinkValues(Response r) {
    List<Header> links = r.headers().getList("Link");
    List<String> linkValues = links.stream()
                                   .map(l -> l.getValue())
                                   .collect(Collectors.toList());
    return linkValues;
  }

  protected String getHeaderValue(Response r, String headerName) {
    return r.header(headerName);
  }

  protected Header getAuthHeader(String username, String password) {
    Credentials credentials = new Credentials("matt", "megan");
    Response response = given().contentType(ContentType.JSON)
                               .body(credentials)
                               .expect()
                               .statusCode(Status.OK.getStatusCode())
                               .contentType(ContentType.JSON)
                               .body(Matchers.any(String.class))
                               .when()
                               .post(basePath + "api/authz");
    return new Header(HttpHeaders.AUTHORIZATION, "Bearer " + response.print());
  }
}
