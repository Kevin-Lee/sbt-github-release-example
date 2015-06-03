resolvers += "Kevin's Public Repository" at "http://nexus.lckymn.com/content/repositories/kevin-public-releases"

addSbtPlugin("org.scala-sbt.plugins" %% "sbt-onejar-ex" % "0.8.1")

resolvers += "Github-API" at "http://repo.jenkins-ci.org/public/"
resolvers += "Era7 maven releases" at "https://s3-eu-west-1.amazonaws.com/releases.era7.com"

addSbtPlugin("ohnosequences" % "sbt-github-release" % "0.2.1")

addSbtPlugin("com.typesafe.sbt" % "sbt-s3" % "0.8")
