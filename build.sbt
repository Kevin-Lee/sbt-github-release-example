import org.apache.commons.io.filefilter.WildcardFileFilter
import sbt.Keys._

import com.github.retronym.SbtOneJar
import com.github.retronym.SbtOneJar._

import ohnosequences.sbt.SbtGithubReleasePlugin._

import S3._

name := """sbt-github-release-example"""

val projectVersion = "1.0.1"

version := projectVersion

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-library" % scalaVersion.value,
  "org.scalatest" %% "scalatest" % "2.2.4" % "test" excludeAll(
    ExclusionRule(organization = "org.scala-lang", name = "scala-reflect"),
    ExclusionRule(organization = "org.scala-lang.modules", name = "scala-xml_2.11")
  )
)

lazy val writeVersion = inputKey[Unit]("Write Version in File'")

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

def wildcardFilter(name: String): java.io.FileFilter = new WildcardFileFilter(name).asInstanceOf[java.io.FileFilter]

def getAllSubDirs(dir: File): Array[File] = dir.listFiles(DirectoryFilter).flatMap(x => x +: getAllSubDirs(x))

def fileList(dir: File, name: String): List[File] = {
  def fileList0(dir: File, name: String): List[File] = dir.listFiles(wildcardFilter(name)).toList
  (dir :: getAllSubDirs(dir).toList).flatMap(fileList0(_, name))
}
def pathNameAndFileList(base: File, dir: String, name: String): List[(File, String)] = {
  val basePath = base.getPath
  val basePathLength = basePath.length + (if (basePath.endsWith(java.io.File.separator)) 0 else 1)
  println(
    s"""
       |basePath: $basePath
       |basePathLength: $basePathLength
     """.stripMargin)
  fileList(base / dir, name).map(f => (f, f.getPath)).map { case (file, parent) => (file, parent.drop(basePathLength)) }
}

lazy val listBinNames = taskKey[Seq[File]]("Prints 'file list'")

listBinNames := {

//  val binNames = (target.value / "scala-2.11").listFiles(new FileFilter {
//    override def accept(pathname: File): Boolean = pathname.getName.endsWith("one-jar.jar")
//  }).toList
//  val binNames = (target.value / "scala-2.11").listFiles(wildcardFilter("*one-jar.jar")).toList
//  val binNames = fileList(target.value / "scala-2.11", "*one-jar.jar")
//  val binNames = fileList(target.value , "*")
  val binNames = pathNameAndFileList(target.value, "" , "*.jar")

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

  val binNames = fileList(target.value / "ci", "*one-jar.jar")

  println(s"fileNames: $binNames")

  binNames
}
/* } GitHub Release */

/* S3 Upload { */
s3Settings

mappings in upload := pathNameAndFileList(target.value, "ci", "*one-jar.jar")

host in upload := sys.env.getOrElse("S3_BUCKET", "")

// progress in upload := true

credentials += Credentials(Path.userHome / ".s3credentials")
/* } S3 Upload */
