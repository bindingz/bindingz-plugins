package io.bindingz.plugin.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.Collection;

public abstract class AbstractBindingzMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    protected MavenProject project;

    @Parameter(property = "registry")
    protected String registry;

    @Parameter(property = "apiKey")
    protected String apiKey;

    @Parameter(defaultValue = "${project.build.directory}/generated-sources/bindingz", property = "targetSourceDirectory")
    protected File targetSourceDirectory;

    @Parameter(defaultValue = "${project.build.directory}/generated-resources/bindingz", property = "targetResourceDirectory")
    protected File targetResourceDirectory;

    @Parameter(property = "processConfigurations")
    protected Collection<ProcessConfiguration> processConfigurations;

    @Parameter(property = "publishConfigurations")
    protected Collection<PublishConfiguration> publishConfigurations;
}
