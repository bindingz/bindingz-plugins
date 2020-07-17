resolvers += Classpaths.sbtPluginReleases

resolvers += Resolver.mavenLocal
resolvers += Resolver.mavenCentral
resolvers += Resolver.sonatypeRepo("releases")

addSbtPlugin("io.bindingz" %% "sbt-bindingz-plugin" % "1.0.0-SNAPSHOT")

addSbtPlugin("de.heikoseeberger" % "sbt-header" % "5.2.0")