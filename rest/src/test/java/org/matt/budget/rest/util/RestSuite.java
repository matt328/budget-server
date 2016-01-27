package org.matt.budget.rest.util;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
                WorkspaceEndpointIT.class,
                AccountEndpointIT.class,
})
public class RestSuite {

}
