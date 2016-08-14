import sbt.Project.projectToRef
import com.trueaccord.scalapb.{ScalaPbPlugin => PB}

name := "wakana"

version := "1.0"

scalaVersion := "2.11.7"

lazy val clients = Seq(client)
lazy val scalaV = "2.11.7"

lazy val protoPaths = Seq(
  file("server/protobuf").getCanonicalFile()
)

lazy val server = (project in file("server")).settings(
  scalaVersion := scalaV,
  scalaJSProjects := clients,
  pipelineStages := Seq(scalaJSProd), // TODO: add gzip
  resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases",
  libraryDependencies ++= Seq(
    "com.vmunier" %% "play-scalajs-scripts" % "0.3.0",
    "org.webjars" % "jquery" % "1.11.1",
    "org.twitter4j" % "twitter4j-core" % "[4.0,)",
    specs2 % Test
  )).settings(
    PB.protobufSettings:_*
  ).settings(
    PB.runProtoc in PB.protobufConfig := { args =>
      com.github.os72.protocjar.Protoc.runProtoc("-v300" +: args.toArray)
    },
    PB.includePaths in PB.protobufConfig ++= protoPaths,
    sourceDirectories in PB.protobufConfig ++= protoPaths,
    PB.grpc in PB.protobufConfig := false
  ).enablePlugins(PlayScala).
  aggregate(clients.map(projectToRef): _*).
  dependsOn(sharedJvm)

lazy val client = (project in file("client")).settings(
  scalaVersion := scalaV,
  persistLauncher := true,
  persistLauncher in Test := false,
  libraryDependencies ++= Seq(
    "be.doeraene"  %%% "scalajs-jquery" % "0.8.0",
    "org.scala-js" %%% "scalajs-dom" % "0.8.0",
    "com.lihaoyi"  %%% "utest" % "0.3.0" % "test"
  )
).enablePlugins(ScalaJSPlugin, ScalaJSPlay).
  dependsOn(sharedJs)

lazy val shared = (crossProject.crossType(CrossType.Pure) in file("shared")).
  settings(scalaVersion := scalaV).
  jsConfigure(_ enablePlugins ScalaJSPlay)

lazy val sharedJvm = shared.jvm
lazy val sharedJs = shared.js

// loads the Play project at sbt startup
onLoad in Global := (Command.process("project server", _: State)) compose (onLoad in Global).value

skip in packageJSDependencies := false

testFrameworks += new TestFramework("utest.runner.Framework")

persistLauncher in Compile := true

persistLauncher in Test := false
