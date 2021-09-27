import CiCommands.{ ciBuild, devBuild }
import sbtrelease.ReleaseStateTransformations.{
  checkSnapshotDependencies,
  commitNextVersion,
  commitReleaseVersion,
  inquireVersions,
  publishArtifacts,
  pushChanges,
  setNextVersion,
  setReleaseVersion,
  tagRelease
}

organization := "io.vangogiel"
name := "halselhof"

scalaVersion := "2.13.0"
crossScalaVersions := Seq("2.11.8", "2.12.10")

scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-feature",
  "-Ywarn-unused:imports",
  "-Ywarn-dead-code",
  "-Xlint:adapted-args",
  "-Xsource:2.13",
  "-Xfatal-warnings"
)

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-json" % "2.9.2",
  "com.typesafe.play" %% "play" % "2.8.8",
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % "test"
)

commands ++= Seq(ciBuild, devBuild)

coverageMinimum := 100
coverageFailOnMinimum := true

releaseVersionBump := sbtrelease.Version.Bump.Next
releaseVersionFile := baseDirectory.value / "version.sbt"
releasePublishArtifactsAction := PgpKeys.publishSigned.value

publishConfiguration := publishConfiguration.value.withOverwrite(true)
releaseIgnoreUntrackedFiles := true
releaseCrossBuild := true

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  publishArtifacts,
  setNextVersion,
  commitNextVersion,
  pushChanges
)

credentials += Credentials(
  "Sonatype Nexus Repository Manager",
  "s01.oss.sonatype.org",
  sys.env.getOrElse("SONATYPE_NEXUS_USERNAME", ""),
  sys.env.getOrElse("SONATYPE_NEXUS_PASSWORD", "")
)

ThisBuild / organization := "io.vangogiel.halselhof"
ThisBuild / organizationName := "vangogiel"
ThisBuild / organizationHomepage := Some(url("http://vangogiel.io/"))

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/vangogiel/halselhof"),
    "scm:git@github.com:vangogiel/halselhof.git"
  )
)
ThisBuild / developers := List(
  Developer(
    id = "vangogiel",
    name = "Norbert Gogiel",
    email = "vangogiel@hotmail.co.uk",
    url = url("http://vangogiel.io")
  )
)

ThisBuild / licenses := List("Apache 2" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt"))
ThisBuild / homepage := Some(url("https://github.com/vangogiel/halselhof"))
ThisBuild / pomIncludeRepository := { _ =>
  false
}
ThisBuild / publishTo := {
  val nexus = "https://s01.oss.sonatype.org/"
  if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
  else Some("releases" at nexus + "service/local/staging/deploy/maven2")
}
ThisBuild / publishMavenStyle := true
