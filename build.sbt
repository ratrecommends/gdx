name := "gdx-utils"

organization := "rat-recommends"

version := "1.0"

scalaVersion := "2.11.7"

scalaSource in Compile := baseDirectory.value / "src"
scalaSource in Test := baseDirectory.value / "src"
javaSource in Compile := baseDirectory.value / "src"
javaSource in Test := baseDirectory.value / "src"
resourceDirectory in Compile := baseDirectory.value / "res"
resourceDirectory in Test := baseDirectory.value / "res"
crossPaths := false

libraryDependencies ++= Seq(
  "com.badlogicgames.gdx" % "gdx" % "1.6.4"
)

scalacOptions ++= Seq(
  "-feature",
  "-language:postfixOps",
  "-language:implicitConversions"
)

publishTo := Some(Resolver.file("file", file("releases")))

publishMavenStyle := true

publishArtifact in Test := false