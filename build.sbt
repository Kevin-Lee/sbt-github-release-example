import org.apache.commons.io.filefilter.WildcardFileFilter
import sbt.Keys._

import com.github.retronym.SbtOneJar
import com.github.retronym.SbtOneJar._

import ohnosequences.sbt.SbtGithubReleasePlugin._

import S3._

name := """sbt-github-release-example"""

val projectVersion = "1.0.2"

version := projectVersion

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-library" % scalaVersion.value,
  "org.scalatest" %% "scalatest" % "2.2.4" % "test" excludeAll(
    ExclusionRule(organization = "org.scala-lang", name = "scala-reflect"),
    ExclusionRule(organization = "org.scala-lang.modules", name = "scala-xml_2.11")
  )
)

import CommonUtils._

lazy val writeVersion = inputKey[Unit]("Write Version in File'")

writeVersion := versionWriter(() => Def.spaceDelimited("filename").parsed)(projectVersion)


lazy val listBinNames = taskKey[Seq[File]]("Prints 'file list'")

listBinNames := {

//  val binNames = (target.value / "scala-2.11").listFiles(new FileFilter {
//    override def accept(pathname: File): Boolean = pathname.getName.endsWith("one-jar.jar")
//  }).toList
//  val binNames = (target.value / "scala-2.11").listFiles(wildcardFilter("*one-jar.jar")).toList
//  val binNames = fileList(target.value / "scala-2.11", "*one-jar.jar")
//  val binNames = fileList(target.value , "*")

  val binNames = fileAndPathNameList(BasePath(target.value), NoPrefix, "*.jar")

  println(s"fileNames: \n${binNames.mkString("\n")}")

  binNames.map(_._1)
}

oneJarSettings
mainClass in oneJar := Some("cc.kevinlee.sbt.onejar.MainApp")

/* GitHub Release { */
GithubRelease.repo := "Kevin-Lee/sbt-github-release-example"

GithubRelease.tag := s"Release-v${projectVersion}"

GithubRelease.releaseName := GithubRelease.tag.value

GithubRelease.commitish := "release"

GithubRelease.notesFile := GithubRelease.notesDir.value / s"${projectVersion}.md"

GithubRelease.releaseAssets := {

  val binNames = listFiles(target.value / "ci", "*one-jar.jar")

  println(s"fileNames: $binNames")

  binNames
}
/* } GitHub Release */

/* S3 Upload { */
s3Settings

mappings in upload := fileAndPathNameList(BasePath(target.value, "ci"), Prefix("test-app"), "*one-jar.jar")

host in upload := sys.env.getOrElse("S3_BUCKET", "")

// progress in upload := true

credentials += Credentials(Path.userHome / ".s3credentials")
/* } S3 Upload */
