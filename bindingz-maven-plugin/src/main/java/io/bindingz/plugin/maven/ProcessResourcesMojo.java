package io.bindingz.plugin.maven;

import io.bindingz.plugin.maven.tasks.ProcessResourcesTask;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.IOException;

@Mojo(name = "processResources", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class ProcessResourcesMojo extends AbstractBindingzMojo {

    public void execute() throws MojoExecutionException {
        if (!targetSourceDirectory.exists()) {
            targetSourceDirectory.mkdirs();
        }
        if (!targetResourceDirectory.exists()) {
            targetResourceDirectory.mkdirs();
        }

        project.addCompileSourceRoot(targetSourceDirectory.getPath());
        if (processConfigurations != null) {
            try {
                new ProcessResourcesTask(registry, apiKey, targetSourceDirectory, targetResourceDirectory, processConfigurations).execute();
            } catch (IOException e) {
                throw new MojoExecutionException(e.getMessage(), e);
            }
        }
    }
}
