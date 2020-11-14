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
import io.bindingz.api.client.{ContractRegistryClient, ContractService}
import io.bindingz.api.configuration.SourceCodeConfiguration
import sbt.Keys._
import sbt.{AutoPlugin, Compile, Def, File, IO, PluginTrigger, Plugins, Setting, Test, inConfig, plugins}

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

    bindingzTargetSourceDirectory := Paths.get(sourceManaged.value.toString, "bindingz").toFile,
    bindingzTargetResourceDirectory := Paths.get(resourceManaged.value.toString, "bindingz").toFile,

    bindingzProcessConfigurations := Seq(),
    bindingzPublishConfigurations := Seq()
  ) ++ Seq(Compile, Test).flatMap(c => inConfig(c)(configScopedSettings))

  def processResources =  Def.task {
    val client = new ContractRegistryClient(bindingzRegistry.value, bindingzApiKey.value, objectMapper)
    val resourceDirectory = bindingzTargetResourceDirectory.value.toString
    val sourceDirectory = bindingzTargetSourceDirectory.value.toString

    bindingzProcessConfigurations.value.flatMap(c => {
      val sourceCodeConfiguration = new SourceCodeConfiguration()
      sourceCodeConfiguration.setPackageName(c.packageName)
      sourceCodeConfiguration.setClassName(c.className)
      sourceCodeConfiguration.setSourceCodeProvider(c.factoryType)
      sourceCodeConfiguration.setProviderConfiguration(c.providerConfiguration.asJava)

      val source = client.generateSources(
        c.namespace,
        c.owner,
        c.contractName,
        c.version,
        sourceCodeConfiguration
      )

      val resourcePath = Paths.get(resourceDirectory, c.namespace, c.owner, c.contractName, c.version)
      resourcePath.getParent.toFile.mkdir()

      IO.write(resourcePath.toFile, objectMapper.writeValueAsString(source.getContent().getSchema))

      source.getSources.asScala.map(s => {
        val sourcePath = Paths.get(sourceDirectory, s.getFile.asScala.toArray:_*)
        sourcePath.getParent.toFile.mkdir()

        IO.write(sourcePath.toFile, s.getContent)
        sourcePath.toFile
      })
    })
  }

  def publishResources =  Def.task {
    val cp: Seq[File] = (fullClasspath in Compile).value.files
    val classLoader = ClassLoaderFactory.createClassLoader(cp)
    val client = new ContractRegistryClient(bindingzRegistry.value, bindingzApiKey.value, objectMapper)
    val contractService = new ContractService(objectMapper)
    bindingzPublishConfigurations.value.map(c => {
      val resources = contractService.create(classLoader, c.scanBasePackage)
      resources.asScala.foreach(client.publishContract)
    })
  }
}
