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

import sbt.{File, settingKey, taskKey}

trait BindingzKeys {
  val bindingzRegistry = settingKey[String]("Bindingz registry")
  val bindingzApiKey = settingKey[String]("Bindingz API Key")

  val bindingzTargetSourceDirectory = settingKey[File]("Target source directory")
  val bindingzTargetResourceDirectory = settingKey[File]("Target resource directory")

  val bindingzPublishConfigurations = settingKey[Seq[BindingzPublishConfiguration]]("Bindingz publish configurations")
  val bindingzProcessConfigurations = settingKey[Seq[BindingzProcessConfiguration]]("Bindingz process configurations")

  val bindingzProcessResources = taskKey[Seq[File]]("Process resources")
  val bindingzPublishResources = taskKey[Unit]("Publish contract code")

  val BindingzProcessConfiguration = io.bindingz.plugin.sbt.BindingzProcessConfiguration
  val BindingzPublishConfiguration = io.bindingz.plugin.sbt.BindingzPublishConfiguration
}
