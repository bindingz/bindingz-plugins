
name := "sbt-plugin-example"
organization := "io.bindingz"
version := "1.0.0-SNAPSHOT"

resolvers += Resolver.mavenLocal
resolvers += Resolver.mavenCentral
resolvers += Resolver.sonatypeRepo("releases")

libraryDependencies ++= Seq(
  "io.bindingz" % "bindingz-annotations" % "1.0.0-SNAPSHOT"
)

bindingzApiKey := "cPrS6nyv.ac5bc2f16417e79f2517531c2f6a0591"
bindingzProcessConfigurations := Seq(
  BindingzProcessConfiguration(
    namespace = "default",
    owner = "sbt-plugin-example",
    contractName = "SampleDto",
    version = "v4",
    packageName = "io.bindingz.sample.latest",
    className = "FooBar",
    providerConfiguration = Map(
      "targetLanguage" -> "SCALA"
    )
  )
)
bindingzPublishConfigurations := Seq(
  BindingzPublishConfiguration("io.bindingz.contract.plugin.example")
)
