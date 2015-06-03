import org.apache.commons.io.filefilter.WildcardFileFilter
import sbt.Keys._

import com.github.retronym.SbtOneJar
import com.github.retronym.SbtOneJar._

import ohnosequences.sbt.SbtGithubReleasePlugin._

name := """sbt-github-release-example"""

version := "1.0"

val projectVersion = "2.11.6"

scalaVersion := projectVersion

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-library" % scalaVersion.value,
  "org.scalatest" %% "scalatest" % "2.2.4" % "test" excludeAll(
    ExclusionRule(organization = "org.scala-lang", name = "scala-reflect"),
    ExclusionRule(organization = "org.scala-lang.modules", name = "scala-xml_2.11")
  )
)

oneJarSettings

lazy val writeVersion = inputKey[Unit]("Write Adonis Version in File'")

writeVersion := {
  println("\n== Writing Version File ==")
  val args: Seq[String] = Def.spaceDelimited("filename").parsed
  println(s"The project version is ${projectVersion}.")

  import IO._

  val filename = args.headOption.map("target/" + _).getOrElse("target/version.sbt")
  val versionFile = new File(filename)
  println(s"write ${projectVersion} into the file: $versionFile")

  write(versionFile, projectVersion, utf8, false)
  println("Done: Writing Version File\n")
}

lazy val listBinNames = taskKey[Seq[File]]("Prints 'Hello World'")

def wildcardFilter(name: String): java.io.FileFilter = new WildcardFileFilter(name).asInstanceOf[java.io.FileFilter]

def fileList(dir: File, name: String): List[File] = dir.listFiles(wildcardFilter(name)).toList

listBinNames := {

//  val binNames = (target.value / "scala-2.11").listFiles(new FileFilter {
//    override def accept(pathname: File): Boolean = pathname.getName.endsWith("one-jar.jar")
//  }).toList
//  val binNames = (target.value / "scala-2.11").listFiles(wildcardFilter("*one-jar.jar")).toList
  val binNames = fileList(target.value / "scala-2.11", "*one-jar.jar")

  println(s"fileNames: $binNames")

  binNames
}

mainClass in oneJar := Some("cc.kevinlee.sbt.onejar.MainApp")

GithubRelease.repo := "Kevin-Lee/sbt-github-release-example"

GithubRelease.tag := s"Release-v${projectVersion}"

GithubRelease.releaseName := GithubRelease.tag.value

GithubRelease.commitish := "release"

GithubRelease.notesFile := GithubRelease.notesDir.value / s"${projectVersion}.md"

GithubRelease.assets := {

  val binNames = fileList(target.value / "scala-2.11", "*one-jar.jar")

  println(s"fileNames: $binNames")

  binNames
}

