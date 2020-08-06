
name := "sbt-plugin-example"
organization := "io.bindingz"
version := "1.0.0-SNAPSHOT"

resolvers += Resolver.mavenLocal
resolvers += Resolver.mavenCentral
resolvers += Resolver.sonatypeRepo("releases")

libraryDependencies ++= Seq(
  "io.bindingz" % "bindingz-annotations" % "1.0.0-SNAPSHOT"
)

bindingzProcessConfigurations := Seq(
  BindingzProcessConfiguration(
    namespace = "payments",
    owner = "billing-service",
    contractName = "InvoiceItem",
    version = "0.1-SNAPSHOT",
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
