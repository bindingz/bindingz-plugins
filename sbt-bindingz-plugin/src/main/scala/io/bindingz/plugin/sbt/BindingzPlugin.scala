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

package io.bindingz.plugin.sbt

import java.nio.file.Paths

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import io.bindingz.api.client.context.definition.JsonDefinitionReader
import io.bindingz.api.client.jackson.JacksonContractService
import io.bindingz.api.client.{ClassGraphTypeScanner, ContractRegistryClient}
import io.bindingz.api.model.SourceCodeConfiguration
import sbt.Keys._
import sbt.{AutoPlugin, Compile, Def, File, Global, IO, PluginTrigger, Plugins, Setting, Test, inConfig, plugins}

import scala.collection.JavaConverters._

object BindingzPlugin extends AutoPlugin {

  override val trigger: PluginTrigger = allRequirements
  override val requires: Plugins = plugins.JvmPlugin

  val objectMapper = new ObjectMapper().registerModule(new DefaultScalaModule())

  object autoImport extends BindingzKeys

  import autoImport._

  // settings to be applied for both Compile and Test
  lazy val configScopedSettings: Seq[Setting[_]] = Seq(
    bindingzProcessResources := processResources.value,
    bindingzPublishResources := publishResources.value,
    sourceGenerators += bindingzProcessResources.taskValue,
    sourceDirectories += bindingzTargetSourceDirectory.value,
    resourceDirectories += bindingzTargetResourceDirectory.value
  )

  override lazy val projectSettings: Seq[Setting[_]] = Seq(
    bindingzRegistry := "",
    bindingzApiKey := "",
    bindingzConfigFileLocation := Paths.get("bindingz.json").toFile,
    bindingzTargetSourceDirectory := Paths.get(sourceManaged.value.toString, "bindingz").toFile,
    bindingzTargetResourceDirectory := Paths.get(resourceManaged.value.toString, "bindingz").toFile
  ) ++ Seq(Compile, Test).flatMap(c => inConfig(c)(configScopedSettings))

  val groupId = {
    Def.taskDyn {
      Def.task {
        loadedBuild.value.allProjectRefs.map(_._1)
          .map { ref => organization.in(ref) }
          .find(_ => true) // just get first for the moment
      }
    }
  }

  val projectId = {
    Def.taskDyn {
      Def.task {
        loadedBuild.value.allProjectRefs.map(_._1)
          .map { ref => name.in(ref) }
          .find(_ => true) // just get first for the moment
      }
    }
  }

  def processResources = Def.task {
    val client = new ContractRegistryClient(bindingzRegistry.value, bindingzApiKey.value, objectMapper)
    val resourceDirectory = bindingzTargetResourceDirectory.value.toString
    val sourceDirectory = bindingzTargetSourceDirectory.value.toString

    val group = groupId.value.map(_.evaluate(settingsData.in(Global).value))
    val project = projectId.value.map(_.evaluate(settingsData.in(Global).value))

    val definition = new JsonDefinitionReader().read(bindingzConfigFileLocation.value.toString)
    Option(definition.getProcess()) match {
      case Some(process) => {
        process.getContracts().asScala.flatMap(c => {
          val sourceCodeConfiguration = new SourceCodeConfiguration()
          sourceCodeConfiguration.setPackageName(c.getPackageName())
          sourceCodeConfiguration.setClassName(c.getClassName())
          sourceCodeConfiguration.setSourceCodeProvider(c.getSourceCodeProvider())
          sourceCodeConfiguration.setProviderConfiguration(c.getSourceCodeConfiguration())
          sourceCodeConfiguration.setParticipantNamespace(group.getOrElse(c.getPackageName()))
          sourceCodeConfiguration.setParticipantName(project.getOrElse(c.getClassName()))

          val source = client.generateSources(
            c.getNamespace(),
            c.getOwner(),
            c.getContractName(),
            c.getVersion(),
            sourceCodeConfiguration
          )

          val resourcePath = Paths.get(
            resourceDirectory,
            c.getNamespace(),
            c.getOwner(),
            c.getContractName(),
            c.getVersion()
          )
          resourcePath.getParent.toFile.mkdir()

          IO.write(resourcePath.toFile, objectMapper.writeValueAsString(source.getContent().getSchema))

          source.getSources.asScala.map(s => {
            val sourcePath = Paths.get(sourceDirectory, s.getFile.asScala.toArray: _*)
            sourcePath.getParent.toFile.mkdir()

            IO.write(sourcePath.toFile, s.getContent)
            sourcePath.toFile
          })
        })
      }
      case None => Seq.empty[File]
    }
  }

  def publishResources = Def.task {
    val cp: Seq[File] = (fullClasspath in Compile).value.files
    val classLoader = ClassLoaderFactory.createClassLoader(cp)
    val client = new ContractRegistryClient(bindingzRegistry.value, bindingzApiKey.value, objectMapper)
    val contractService = new JacksonContractService(new ClassGraphTypeScanner(classLoader))

    val definition = new JsonDefinitionReader().read(bindingzConfigFileLocation.value.toString)
    Option(definition.getPublish) match {
      case Some(p) => {
        val resources = contractService.create(p.getContracts())
        resources.asScala.foreach(client.publishContract)
      }
      case None =>
    }
  }
}
