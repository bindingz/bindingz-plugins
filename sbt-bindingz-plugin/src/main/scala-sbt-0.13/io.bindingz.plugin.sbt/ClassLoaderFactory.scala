package io.bindingz.plugin.sbt

import sbt.File

import scala.tools.nsc.util.ScalaClassLoader.URLClassLoader

object ClassLoaderFactory {
  def createClassLoader(cp: Seq[File]): ClassLoader = {
    new URLClassLoader(cp.map(c => c.toURI.toURL), this.getClass.getClassLoader)
  }
}
