package io.bindingz.plugin.maven;


import org.apache.maven.plugin.testing.MojoRule;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ProcessResourcesMojoTest {
    @Rule
    public MojoRule rule = new MojoRule() {
        @Override
        protected void before() throws Throwable {
        }

        @Override
        protected void after() {
        }
    };

    @Test
    public void testProcessResources() throws Exception {
        File pom = new File( "target/test-classes/bindingz-maven-plugin-example" );
        assertNotNull( pom );
        assertTrue(pom.exists());

//        ProcessResourcesMojo processResources = (ProcessResourcesMojo) rule.lookupConfiguredMojo(pom, "processResources");
//        assertNotNull(processResources);
//        processResources.execute();
//
//        PublishResourcesMojo publishResources = (PublishResourcesMojo) rule.lookupConfiguredMojo(pom, "publishResources");
//        assertNotNull(publishResources);
//        publishResources.execute();
    }
}

