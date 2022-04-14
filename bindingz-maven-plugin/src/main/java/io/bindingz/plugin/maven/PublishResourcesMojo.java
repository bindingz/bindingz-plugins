package io.bindingz.plugin.maven;

import io.bindingz.api.client.context.definition.JsonDefinitionReader;
import io.bindingz.api.client.context.definition.model.Definition;
import io.bindingz.plugin.maven.tasks.PublishResourcesTask;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mojo(name = "publishResources", defaultPhase = LifecyclePhase.NONE, requiresDependencyResolution = ResolutionScope.COMPILE)
public class PublishResourcesMojo extends AbstractBindingzMojo {

    public void execute() throws MojoExecutionException {
        Definition definition = new JsonDefinitionReader().read(configFileLocation);
        if (definition.getPublish() != null) {
            try {
                List<URL> urls = new ArrayList<>();
                for (String element : project.getCompileClasspathElements()) {
                    urls.add(new File(element).toURI().toURL());
                }
                ClassLoader classLoader = new URLClassLoader(urls.toArray(new URL[]{}), this.getClass().getClassLoader());
                Arrays.stream(((URLClassLoader) classLoader).getURLs()).forEach(x -> System.out.println(x));
                new PublishResourcesTask(
                        registry,
                        apiKey,
                        definition.getPublish(),
                        classLoader
                ).execute();
            } catch (IOException e) {
                throw new MojoExecutionException(e.getMessage(), e);
            } catch (DependencyResolutionRequiredException e) {
                throw new MojoExecutionException(e.getMessage(), e);
            }
        }
    }
}
