resolvers += "Kevin's Public Repository" at "http://nexus.lckymn.com/content/repositories/kevin-public-releases"

addSbtPlugin("org.scala-sbt.plugins" %% "sbt-onejar-ex" % "0.8.1")

resolvers += "Github-API" at "http://repo.jenkins-ci.org/public/"

resolvers += "Era7 maven releases" at "https://s3-eu-west-1.amazonaws.com/releases.era7.com"
addSbtPlugin("ohnosequences" % "sbt-github-release" % "0.3.0")

//resolvers += Resolver.url("bintray-kevinlee-maven", url("http://dl.bintray.com/kevinlee/maven"))(Resolver.ivyStylePatterns)
//
//addSbtPlugin("ohnosequences" % "sbt-github-release" % "0.2.2")

addSbtPlugin("com.typesafe.sbt" % "sbt-s3" % "0.8")
