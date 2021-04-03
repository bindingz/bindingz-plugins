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

package io.bindingz.plugin.gradle.tasks

import com.fasterxml.jackson.databind.ObjectMapper
import io.bindingz.api.client.ContractRegistryClient
import io.bindingz.api.client.context.definition.JsonDefinitionReader
import io.bindingz.api.model.SourceCodeConfiguration
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

class ProcessResourcesTask extends DefaultTask {

    @Internal
    String registry;

    @Internal
    String apiKey;

    @OutputDirectory
    File targetSourceDirectory;

    @OutputDirectory
    File targetResourceDirectory;

    @InputFile
    File configFileLocation

    ObjectMapper mapper = new ObjectMapper()

    ProcessResourcesTask() {
        description = 'Generates Java classes from a json schema.'
        group = 'Build'
        setOnlyIf { true }
        outputs.upToDateWhen { false }
    }

    @TaskAction
    def generate() {
        def client = new ContractRegistryClient(registry, apiKey, mapper)
        def definition = new JsonDefinitionReader().read(configFileLocation.toString());
        if (definition.getProcess() != null) {
            definition.getProcess().getContracts().forEach { c ->
                def configuration = new SourceCodeConfiguration()
                configuration.setClassName(c.getClassName())
                configuration.setPackageName(c.getPackageName())
                configuration.setSourceCodeProvider(c.getSourceCodeProvider())
                configuration.setProviderConfiguration(c.getSourceCodeConfiguration())
                configuration.setParticipantNamespace(getProject().getGroup())
                configuration.setParticipantName(getProject().getName())

                def resource = client.generateSources(c.namespace, c.owner, c.contractName, c.version, configuration)
                if (resource != null) {
                    if (resource.getSources() != null) {
                        resource.getSources().forEach { s ->
                            try {
                                def path = Paths.get(
                                        targetSourceDirectory.getAbsolutePath(),
                                        s.getFile().toArray(new String[s.getFile().size()])
                                )
                                path.toFile().getParentFile().mkdirs()
                                Files.write(path, s.getContent().getBytes(), StandardOpenOption.CREATE)
                            } catch (IOException e) {
                                e.printStackTrace()
                            }
                        };
                    }

                    if (resource.getContent() != null) {
                        def schema = resource.getContent()
                        try {
                            def path = Paths.get(
                                    targetResourceDirectory.getAbsolutePath(),
                                    schema.getNamespace(),
                                    schema.getOwner(),
                                    schema.getContractName(),
                                    schema.getVersion()
                            )
                            path.toFile().getParentFile().mkdirs()
                            Files.write(path, mapper.writeValueAsBytes(schema.getSchema()), StandardOpenOption.CREATE)
                        } catch (IOException e) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }
}
