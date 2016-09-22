package org.matt.budget.rest.util;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;
import org.matt.budget.models.Workspace;

import com.jayway.restassured.response.Header;
import com.jayway.restassured.response.Response;

public abstract class BaseRestIT {

  protected static final String WEB_INF = "src/main/webapp/WEB-INF";

  protected static final String ACCOUNTS_LINK = "<http://0.0.0.0:8080/test/api/workspaces/${workspaceId}/accounts>; rel=\"accounts\"";
  protected static final String ACCOUNT_SELF_LINK = "<http://0.0.0.0:8080/test/api/workspaces/${workspaceId}/accounts/${accountId}>; rel=\"self\"";
  protected static final String ACCOUNTS_URI = "http://0.0.0.0:8080/test/api/workspaces/${workspaceId}/accounts/${accountId}";

  protected static final String WORKSPACE_SELF_LINK = "<http://0.0.0.0:8080/test/api/workspaces/${workspaceId}>; rel=\"self\"";
  protected static final String WORKSPACE_URI = "http://0.0.0.0:8080/test/api/workspaces/${workspaceId}";

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

}
