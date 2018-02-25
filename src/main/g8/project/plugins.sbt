resolvers += "sonatype-releases" at "https://oss.sonatype.org/content/repositories/releases/"

resolvers += "Spark Package Main Repo" at "https://dl.bintray.com/spark-packages/maven"

addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.1.0")

resolvers += "Artima Maven Repository" at "http://repo.artima.com/releases"

// fat jar
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.6")

// like mvn release plugin
addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.7")

// test coverage sbt clean coverage test
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.5.1")

// check codestyle
addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "1.0.0")

// plot graph of dependencies by running sbt dependencyBrowseGraph
addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.9.0")

// quicker development turnaround
addSbtPlugin("io.spray" % "sbt-revolver" % "0.9.1")

// nicer dependency resolution
addSbtPlugin("io.get-coursier" % "sbt-coursier" % "1.0.2")

// find outdated dependencies by calling sbt dependencyUpdatesReport
addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.3.4")

// prevent nasty errors when coding
addSbtPlugin("org.wartremover" % "sbt-wartremover" % "2.2.1")
addSbtPlugin("com.artima.supersafe" % "sbtplugin" % "1.1.3")

// nice formatting of sourcecode
addSbtPlugin("com.lucidchart" % "sbt-scalafmt-coursier" % "1.15")