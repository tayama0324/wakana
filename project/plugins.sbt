logLevel := Level.Warn

addSbtPlugin("com.vmunier" % "sbt-play-scalajs" % "0.2.8")

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.5.0")

addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.5")

addSbtPlugin("com.trueaccord.scalapb" % "sbt-scalapb" % "0.5.38")

libraryDependencies += "com.github.os72" % "protoc-jar" % "3.0.0"
