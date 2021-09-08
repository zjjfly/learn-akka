
name := "akka-db"
organization := "com.jjzi"
version := "0.1-SNAPSHOT"

scalaVersion := "2.12.7"

val commonLibraries = Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.4.16",
  "com.typesafe.akka" % "akka-slf4j_2.12" % "2.4.16",
  "org.scalactic" %% "scalactic" % "3.0.5",
  "org.scalatest" %% "scalatest" % "3.0.5" % "test"
  )
libraryDependencies ++= commonLibraries
val commonSettings = Seq(
  organization := "com.jjzi",
  version := "0.1-SNAPSHOT",
  )
lazy val db_message = (project in file("akka-db-message"))
  .settings(commonSettings)
  .settings(
    Seq(
      name := "akka-db-message",
      libraryDependencies ++= commonLibraries
      ))
lazy val db_client = (project in file("akka-db-client"))
  .settings(commonSettings)
  .settings(
    Seq(
      name := "akka-db-client",
      libraryDependencies ++= Seq(
        "com.typesafe.akka" %% "akka-remote" % "2.4.16"
        ) ++ commonLibraries
      ))
  .dependsOn(db_message)
lazy val db_server = (project in file("akka-db-server"))
  .settings(commonSettings)
  .settings(Seq(
    name := "akka-db-server",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-remote" % "2.4.16",
      "com.typesafe.akka" %% "akka-testkit" % "2.4.16" % "test"
      ) ++ commonLibraries
    ))
  .dependsOn(db_message)
lazy val rss = (project in file("akka-rss"))
  .settings(commonSettings)
  .settings(
    Seq(
      name := "akka-rss",
      libraryDependencies ++= Seq(
        "com.syncthemall" % "boilerpipe" % "1.2.2"
        ) ++ commonLibraries
      ))
  .dependsOn(db_message)
lazy val supervision = (project in file("akka-supervision"))
  .settings(commonSettings)
  .settings(Seq(
    name := "akka-supervision",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-remote" % "2.4.16",
      "com.typesafe.akka" %% "akka-testkit" % "2.4.16" % "test"
      ) ++ commonLibraries
    ))
