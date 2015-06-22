name := "ScalaApp"

version := "1.0"

scalaVersion := "2.11.5"

scalacOptions ++= Seq("-unchecked", "-deprecation", "-encoding", "utf8")

javacOptions ++= Seq("-Xlint:deprecation", "-encoding", "utf8","-XX:MaxPermSize=256m")

libraryDependencies ++= {
  val akkaGroup = "com.typesafe.akka"
  val akkaVersion = "2.3.10"
  val akkaStreamVersion = "1.0-RC2"
  val scalaTestVersion = "2.2.4"
  Seq(
    akkaGroup %% "akka-actor" % akkaVersion,
    akkaGroup %% "akka-stream-experimental" % akkaStreamVersion,
    akkaGroup %% "akka-http-core-experimental" % akkaStreamVersion,
    //akkaGroup %% "akka-http-experimental" % akkaStreamVersion,
    akkaGroup %% "akka-http-scala-experimental" % akkaStreamVersion,
    akkaGroup %% "akka-http-spray-json-experimental" % akkaStreamVersion,
    akkaGroup %% "akka-http-testkit-scala-experimental" % akkaStreamVersion % "test",
    "org.scalatest" %% "scalatest" % scalaTestVersion % "test"
  )
}

mainClass in (Compile, run) := Some("org.bluecollar.akka_http.example.WebServer")

mainClass in assembly := Some("org.bluecollar.akka_http.example.WebServer")

assemblyJarName in assembly := "scala-learn-app.jar"
