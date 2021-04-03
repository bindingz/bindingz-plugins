/**
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

package io.bindingz.plugin.gradle


import io.bindingz.plugin.gradle.tasks.PublishResourcesTask
import io.bindingz.plugin.gradle.extension.BindingzExtension

import io.bindingz.plugin.gradle.tasks.ProcessResourcesTask
import org.gradle.api.Plugin
import org.gradle.api.Project

class BindingzPlugin implements Plugin<Project> {

    public static final String EXTENSION_NAME = 'bindingz'

    @Override
    void apply(Project project) {
        setupExtension(project)
        configureDirectories(project)
        createTasks(project)
    }

    private def setupExtension(final Project project) {
        project.extensions.create(EXTENSION_NAME, BindingzExtension)
    }

    def configureDirectories(final Project project) {
        project.afterEvaluate {
            def configuration = project.extensions.getByType(BindingzExtension)

            configuration.targetSourceDirectory = configuration.targetSourceDirectory ?: project.file("target/generated-sources/bindingz")
            configuration.targetResourceDirectory = configuration.targetResourceDirectory ?: project.file("target/generated-resources/bindingz")
            configuration.configFileLocation = configuration.configFileLocation ?: project.file("bindingz.json")

            configuration.targetSourceDirectory.mkdirs()
            configuration.targetResourceDirectory.mkdirs()
        }
    }

    def createTasks(final Project project) {
        project.afterEvaluate {
            def configuration = project.extensions.getByType(BindingzExtension)

            createProcessResourcesTask(project, configuration)
            createPublishResourcesTask(project, configuration)
        }
    }

    private void createProcessResourcesTask(Project project, BindingzExtension configuration) {
        def processResourcesTask = project.tasks.create "bindingzProcessResources", ProcessResourcesTask
        processResourcesTask.group = "bindingz"
        processResourcesTask.description = "Process bindingz resources"
        processResourcesTask.registry = configuration.registry
        processResourcesTask.apiKey = configuration.apiKey

        processResourcesTask.targetSourceDirectory = configuration.targetSourceDirectory
        processResourcesTask.targetResourceDirectory = configuration.targetResourceDirectory
        processResourcesTask.configFileLocation = configuration.configFileLocation

        project.sourceSets.main.resources.srcDirs += [configuration.targetResourceDirectory]

        if (project.plugins.hasPlugin('java')) {
            project.sourceSets.main.java.srcDirs += [configuration.targetSourceDirectory]
            project.tasks.compileJava.dependsOn(processResourcesTask)
        }

        if (project.plugins.hasPlugin('scala')) {
            project.sourceSets.main.scala.srcDirs += [configuration.targetSourceDirectory]
            project.tasks.compileScala.dependsOn(processResourcesTask)
        }
    }

    private void createPublishResourcesTask(Project project, BindingzExtension configuration) {
        def publishResourcesTask = project.tasks.create "bindingzPublishResources", PublishResourcesTask
        publishResourcesTask.group = "bindingz"
        publishResourcesTask.description = "Publish bindingz resources"
        publishResourcesTask.registry = configuration.registry
        publishResourcesTask.apiKey = configuration.apiKey
        publishResourcesTask.configFileLocation = configuration.configFileLocation

        Set<File> classFiles = project.sourceSets.main.runtimeClasspath.getFiles()
        URL[] urls = classFiles.collect { it.toURI().toURL() }.toArray() as URL[]
        publishResourcesTask.classLoader = new URLClassLoader(urls, this.getClass().getClassLoader())

        if (project.plugins.hasPlugin('java')) {
            publishResourcesTask.dependsOn(project.tasks.compileJava)
        }

        if (project.plugins.hasPlugin('scala')) {
            publishResourcesTask.dependsOn(project.tasks.compileScala)
        }
    }
}
