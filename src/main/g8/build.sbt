// Copyright (C) 2018

name := "$name$"
organization in ThisBuild := "$organization$"
scalaVersion in ThisBuild := "2.11.12"

// PROJECTS

lazy val global = project
  .in(file("."))
  .settings(
    settings,
    libraryDependencies ++= commonDependencies
  )
  .aggregate(
    common,
    job1,
    jobTypesafe
  )
  .dependsOn(
    common,
    job1,
    jobTypesafe
  )
  .disablePlugins(AssemblyPlugin) // do not output a useless jar for the root project

lazy val common = project
  .settings(
    name := "common",
    settings,
    libraryDependencies ++= commonDependencies
  )

lazy val job1 = project
  .settings(
    name := "job1",
    settings,
    commonAssemblySettings,
    libraryDependencies ++= commonDependencies,
    mainClass in assembly := Some("$organization$.$name$.app.Job1")
  )
  .dependsOn(
    common
  )

lazy val jobTypesafe = project
  .settings(
    name := "jobTypesafe",
    settings,
    commonAssemblySettings,
    libraryDependencies ++= commonDependencies ++ Seq(
      dependencies.framelessDataset,
      dependencies.framelessCats
    ),
    mainClass in assembly := Some("$organization$.$name$.app.JobTypesafe")
  )
  .dependsOn(
    common
  )

// DEPENDENCIES

lazy val dependencies =
  new {
    val sparkV        = "$sparkVersion$"
    val sparkTestingV = "0.8.0"
    val configsV      = "0.4.4"
    val oracleJDBCV   = "12.2.0.1"
    val postgresJDBCV = "42.1.4"
    val geosparkV     = "1.0.1"
    val framelessV    = "0.5.0"

    val sparkBase = "org.apache.spark" %% "spark-core" % sparkV % "provided"
    val sparkSql  = "org.apache.spark" %% "spark-sql"  % sparkV % "provided"
    val sparkHive = "org.apache.spark" %% "spark-hive" % sparkV % "provided"
    val sparkMl = "org.apache.spark" %% "spark-mllib" % sparkV % "provided"
    val sparkStreaming = "org.apache.spark" %% "spark-streaming" % sparkV % "provided"
    val sparkStreamingKafka = "org.apache.spark" %% "spark-streaming-kafka-0-10" % sparkV

    val configs      = "com.github.kxbmap" %% "configs"     % configsV

    val framelessDataset = "org.typelevel" %% "frameless-dataset" % framelessV,
    val framelessMl = "org.typelevel" %% "frameless-ml"      % framelessV,
    val framelessCats = "org.typelevel" %% "frameless-cats"    % framelessV,
    
    val scalaTest = "org.scalatest" %% "scalatest" % "3.0.4" % "test"
    val scalaCheck = "org.scalacheck" %% "scalacheck" % "1.13.5" % "test"
    val sparkTesting = "com.holdenkarau" %% "spark-testing-base" % "$sparkTestingbaseRelease$" % "test"
  }

lazy val commonDependencies = Seq(
  dependencies.sparkBase,
  dependencies.sparkHive,
  dependencies.sparkSql,
  dependencies.configs,
  dependencies.sparkTesting,
  dependencies.scalaCheck,
  dependencies.scalaTest
)

// SETTINGS

lazy val settings =
commonSettings ++
wartremoverSettings ++
scalafmtSettings

lazy val commonSettings = Seq(
  //The default SBT testing java options are too small to support running many of the tests
  // due to the need to launch Spark in local mode.
  javacOptions ++= Seq("-source", "1.8", "-target", "1.8"),
  javaOptions ++= Seq("-Xms512M", "-Xmx2048M", "-XX:MaxPermSize=2048M", "-XX:+CMSClassUnloadingEnabled"),
  scalacOptions ++= Seq(
    "-target:jvm-1.8",
    "-encoding",
    "UTF-8",
    "-feature",
    "-unchecked",
    "-deprecation",
    "-Xfuture",
    "-Xlint:missing-interpolator",
    "-Yno-adapted-args",
    "-Ywarn-dead-code",
    "-Ywarn-numeric-widen",
    "-Ywarn-value-discard",
    "-Ywarn-dead-code",
    "-Ywarn-unused"
  ),
  parallelExecution in Test := false,
  coverageHighlighting := true,
  resolvers ++= Seq(
    "Artima Maven Repository" at "http://repo.artima.com/releases",
    "sonatype-releases" at "https://oss.sonatype.org/content/repositories/releases/",
    "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
    "Second Typesafe repo" at "http://repo.typesafe.com/typesafe/maven-releases/",
    Resolver.sonatypeRepo("public")
  ),
  fork := true,
  fullClasspath in reStart := (fullClasspath in Compile).value,
  run in Compile := Defaults
    .runTask(fullClasspath in Compile, mainClass.in(Compile, run), runner.in(Compile, run))
    .evaluated,
  // pomIncludeRepository := { x => false },
  publishMavenStyle := true,
  // publish settings, also see https://github.com/xerial/sbt-sonatype
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value) {
      Some("snapshots" at nexus + "content/repositories/snapshots")
    } else {
      Some("releases" at nexus + "service/local/staging/deploy/maven2")
    }
  }
)

lazy val wartremoverSettings = Seq(
  wartremoverWarnings in (Compile, compile) ++= Warts.allBut(Wart.Throw,
                                                             Wart.Any,
                                                             Wart.PublicInference,
                                                             Wart.NonUnitStatements,
                                                             Wart.DefaultArguments,
                                                             Wart.ImplicitParameter,
                                                             Wart.Nothing)
)

lazy val scalafmtSettings =
  Seq(
    scalafmtOnCompile := true,
    scalafmtTestOnCompile := true,
    scalafmtVersion := "1.4.0"
  )

lazy val commonAssemblySettings = Seq(
  assemblyJarName in assembly := name.value + ".jar",
  assemblyMergeStrategy in assembly := {
    case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
    case PathList("rootdoc.txt")             => MergeStrategy.discard
    case _                                   => MergeStrategy.deduplicate
  },
  assemblyShadeRules in assembly := Seq(ShadeRule.rename("shapeless.**" -> "new_shapeless.@1").inAll),
  test in assembly := {}
)

initialCommands in console :=
  """
    |import $organization$.$name$.utils.config.ConfigurationUtils
    |import configs.Configs
    |import org.apache.spark.sql.SparkSession
    |import org.slf4j.LoggerFactory
    |import $organization$.$name$.config.Job1Configuration
    |import org.apache.spark.sql.functions._
    |
    |@transient lazy val logger = LoggerFactory.getLogger("console")
    |val c = ConfigurationUtils.loadConfiguration[Job1Configuration]
    |val spark = ConfigurationUtils.createSparkSession("console", true)
    |
    |import spark.implicits._
  """.stripMargin
