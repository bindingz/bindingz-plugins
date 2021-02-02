package io.bindingz.plugin.sbt

import sbt.File

import scala.reflect.internal.util.ScalaClassLoader

object ClassLoaderFactory {
  def createClassLoader(cp: Seq[File]): ClassLoader = {
    ScalaClassLoader.fromURLs(cp.map(c => c.toURI.toURL))
  }
}
