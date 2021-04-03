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

import io.bindingz.api.client.ClassGraphTypeScanner
import io.bindingz.api.client.ContractRegistryClient
import io.bindingz.api.client.context.definition.JsonDefinitionReader
import io.bindingz.api.client.jackson.JacksonContractService
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

class PublishResourcesTask extends DefaultTask {

    @Internal
    String registry

    @Internal
    String apiKey

    @Internal
    ClassLoader classLoader;

    @InputFile
    File configFileLocation

    PublishResourcesTask() {
        description = 'Publishes contract.'
        group = 'Build'
    }

    @TaskAction
    def generate() {
        def client = new ContractRegistryClient(registry, apiKey)
        def service = new JacksonContractService(new ClassGraphTypeScanner(classLoader))
        def definition = new JsonDefinitionReader().read(configFileLocation.toString());
        if (definition.getPublish() != null) {
            service.create(definition.getPublish().getContracts()).forEach { c ->
                client.publishContract(c)
            }
        }
    }
}
