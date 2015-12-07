name := "Automerging"

version := "1.1"

scalaVersion := "2.11.4"

fork:= true

mainClass in (Compile, run) := Some("main.scala.merger.AutoMerge")

resolvers += "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  "org.specs2" %% "specs2-core" % "3.6.5" % "test",
  "org.apache.spark" %% "spark-core" % "1.5.0",
  "org.apache.spark" %% "spark-sql" % "1.5.0",
  "mysql" % "mysql-connector-java" % "5.1.34",
  "org.apache.commons" % "commons-lang3" % "3.3.2")

scalacOptions in Test ++= Seq("-Yrangepos")