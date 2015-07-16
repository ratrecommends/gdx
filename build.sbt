name := "Gdx utils"
organization := "com.github.ratrecommends"
organizationName := "Rat Recommends Games"
homepage := Some(url("https://github.com/ratrecommends/gdx/"))
licenses += "BSD New" -> url("http://opensource.org/licenses/BSD-3-Clause")
description := "Some helpful scala utils for libgdx"
version := "0.1"
scmInfo := Some(ScmInfo(
  url("git@github.com:ratrecommends/gdx.git"),
  "scm:git:git@github.com:ratrecommends/gdx.git"
))
pomExtra := {
  <developers>
    <developer>
      <id>vlaaad</id>
      <name>Vlad Protsenko</name>
      <email>vlad_kontakt@mail.ru</email>
      <url>https://github.com/vlaaad/</url>
    </developer>
  </developers>
}

useGpg := true


scalaVersion := "2.11.7"

scalaSource in Compile := baseDirectory.value / "src"
scalaSource in Test := baseDirectory.value / "src"

javaSource in Compile := baseDirectory.value / "src"
javaSource in Test := baseDirectory.value / "src"

resourceDirectory in Compile := baseDirectory.value / "res"
resourceDirectory in Test := baseDirectory.value / "res"

crossPaths := false

libraryDependencies += "com.badlogicgames.gdx" % "gdx" % "1.6.4"

scalacOptions ++= Seq(
  "-feature",
  "-language:postfixOps",
  "-language:implicitConversions"
)

publishTo := Some(Resolver.file("file", file("releases")))

publishMavenStyle := true

publishArtifact in Test := false