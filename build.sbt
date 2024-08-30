ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.5.0"

lazy val root = (project in file("."))
  .settings(
    name := "catsactorsexamples"
  )

resolvers += "jitpack" at "https://jitpack.io"

libraryDependencies += "com.github.suprnation.cats-actors" %% "cats-actors" % "2.0.0-RC4"
