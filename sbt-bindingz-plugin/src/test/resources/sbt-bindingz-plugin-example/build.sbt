name := "sbt-bindingz-plugin-example"
organization := "io.bindingz"
version := "1.0.0-SNAPSHOT"

resolvers += Resolver.mavenLocal
resolvers += Resolver.mavenCentral
resolvers += Resolver.sonatypeRepo("releases")

libraryDependencies ++= Seq(
  "io.bindingz" % "bindingz-annotations" % "1.0.2",
  "com.fasterxml.jackson.core" % "jackson-core" % "2.9.8",
  "com.fasterxml.jackson.core" % "jackson-annotations" % "2.9.8"
)

bindingzProcessConfigurations := Seq(
  BindingzProcessConfiguration(
    namespace = "default",
    owner = "sbt-bindingz-plugin-example",
    contractName = "SampleDto",
    version = "v5",
    packageName = "io.bindingz.sample.latest",
    className = "InvoiceItemDto",
    providerConfiguration = Map(
      "targetLanguage" -> "SCALA"
    )
  )
)
bindingzPublishConfigurations := Seq(
  BindingzPublishConfiguration("io.bindingz.contract.plugin.example")
)
