/*
 * Copyright (c) 2020 Connor Goulding
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.bindingz.plugin.maven.tasks;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.bindingz.api.client.ContractRegistryClient;
import io.bindingz.api.client.context.definition.model.ProcessConfiguration;
import io.bindingz.api.client.context.definition.model.ProcessContractDefinition;
import io.bindingz.api.model.ContractDto;
import io.bindingz.api.model.SourceCodeConfiguration;
import io.bindingz.api.model.SourceResource;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class ProcessResourcesTask implements ExecutableTask {

    private final ObjectMapper mapper = new ObjectMapper();

    private final String registry;
    private final String apiKey;
    private final MavenProject mavenProject;
    private final File targetSourceDirectory;
    private final File targetResourceDirectory;
    private final ProcessConfiguration processConfiguration;

    public ProcessResourcesTask(
            String registry,
            String apiKey,
            MavenProject mavenProject,
            File targetSourceDirectory,
            File targetResourceDirectory,
            ProcessConfiguration processConfiguration) {
        this.apiKey = apiKey;
        this.registry = registry;
        this.mavenProject = mavenProject;
        this.targetSourceDirectory = targetSourceDirectory;
        this.targetResourceDirectory = targetResourceDirectory;
        this.processConfiguration = processConfiguration;
    }

    public void execute() throws IOException {
        ContractRegistryClient client = new ContractRegistryClient(registry, apiKey, mapper);
        for (ProcessContractDefinition c : processConfiguration.getContracts()) {
            SourceCodeConfiguration configuration = new SourceCodeConfiguration();
            configuration.setClassName(c.getClassName());
            configuration.setPackageName(c.getPackageName());
            configuration.setSourceCodeProvider(c.getSourceCodeProvider());
            configuration.setProviderConfiguration(c.getSourceCodeConfiguration());
            configuration.setParticipantNamespace(mavenProject.getGroupId());
            configuration.setParticipantName(mavenProject.getArtifactId());

            SourceResource resource = client.generateSources(c.getNamespace(), c.getOwner(), c.getContractName(), c.getVersion(), configuration);
            if (resource != null) {
                if (resource.getSources() != null) {
                    resource.getSources().stream().forEach(s -> {
                        try {
                            Path path = Paths.get(
                                    targetSourceDirectory.getAbsolutePath(),
                                    s.getFile().toArray(new String[s.getFile().size()])
                            );
                            path.toFile().getParentFile().mkdirs();
                            Files.write(path, s.getContent().getBytes(), StandardOpenOption.CREATE);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }

                if (resource.getContent() != null) {
                    ContractDto schema = resource.getContent();
                    try {
                        Path path = Paths.get(
                                targetResourceDirectory.getAbsolutePath(),
                                schema.getNamespace(),
                                schema.getOwner(),
                                schema.getContractName(),
                                schema.getVersion()
                        );
                        path.toFile().getParentFile().mkdirs();
                        Files.write(path, mapper.writeValueAsBytes(schema.getSchema()), StandardOpenOption.CREATE);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
